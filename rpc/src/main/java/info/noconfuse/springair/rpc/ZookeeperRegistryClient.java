/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Zheng Zhipeng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package info.noconfuse.springair.rpc;

import info.noconfuse.springair.rpc.provider.ZookeeperServiceRegistration;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Use zookeeper as registry of services.
 * This abstract class provides the ablility of connecting to zookeeper server.
 *
 * @author Zheng Zhipeng
 */
public abstract class ZookeeperRegistryClient {

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperRegistryClient.class);

    private final String registryAddress;

    private String nameSpace;

    private CuratorFramework zookeeperClient;

    public static final String DEFAULT_NAMESPACE = "services_registry";

    public static final String DEFAULT_SERVICES_TOP_PATH = "/services";
    public static final String DEFAULT_CONSUMERS_TOP_PATH = "/consumers";
    public static final String DEFAULT_HISTORY_TOP_PATH = "/history";

    public ZookeeperRegistryClient(String registryAddress) {
        this(registryAddress, null);
    }

    public ZookeeperRegistryClient(String registryAddress, String nameSpace) {
        Assert.notNull(registryAddress, "Missing zookeeper server address.");
        this.registryAddress = registryAddress;
        if (nameSpace == null) {
            LOG.warn("Registry namespace is not specified, will use default value:{}", DEFAULT_NAMESPACE);
            this.nameSpace = DEFAULT_NAMESPACE;
        } else {
            this.nameSpace = nameSpace;
        }
    }

    @PostConstruct
    protected void connectZookeeper() throws Exception {
        if (zookeeperClient == null) {
            synchronized (ZookeeperServiceRegistration.class) {
                if (zookeeperClient == null) {
                    RetryPolicy retryPolicy = new BoundedExponentialBackoffRetry(500, 3000, 5);
                    zookeeperClient = CuratorFrameworkFactory.builder()
                            .namespace(nameSpace)
                            .connectString(registryAddress)
                            .retryPolicy(retryPolicy)
                            .connectionTimeoutMs(5000)
                            .sessionTimeoutMs(3000)
                            .build();
                    zookeeperClient.start();
                }
            }
        }
        LOG.info("Connected to zookeeper:{}", registryAddress);
    }

    @PreDestroy
    protected void disconnectZookeeper() {
        if (zookeeperClient != null) {
            zookeeperClient.close();
            LOG.info("Disonnected to zookeeper server.");
        }
    }

    /**
     * Create an ephemeral node.
     */
    protected void createEphemeralNode(String path, byte[] data) throws Exception {
        getZookeeperClient().create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, data);
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public CuratorFramework getZookeeperClient() {
        return zookeeperClient;
    }
}
