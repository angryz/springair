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

package info.noconfuse.springair.rpc.provider;

import info.noconfuse.springair.rpc.LocalHostUtils;
import info.noconfuse.springair.rpc.ZookeeperRegistryClient;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Use zookeeper as service registry. Connect to zookeeper then create nodes for services.
 *
 * @author Zheng Zhipeng
 */
public class ZookeeperServiceRegistration extends ZookeeperRegistryClient implements ServiceRegistration {

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperServiceRegistration.class);

    private String hostName;
    private String ip;
    private int port;

    public ZookeeperServiceRegistration(String registryAddress) {
        this(registryAddress, null);
    }

    public ZookeeperServiceRegistration(String registryAddress, String nameSpace) {
        super(registryAddress, nameSpace);
        hostName = LocalHostUtils.hostName();
        ip = LocalHostUtils.ip();
        port = LocalHostUtils.serverPort();
        Assert.isTrue(port > 0, "Can not find local server port config 'server.port' or 'tomcat.server.port'");
    }

    @Override
    public void registerService(String serviceName) throws Exception {
        String url = makeServiceUrl(serviceName);
        // create ephemral node for service instance
        String serviceInstName = hostName + "_" + port;
        createEphemeralNode(ZKPaths.makePath(DEFAULT_SERVICES_TOP_PATH, serviceName, serviceInstName),
                url.getBytes("UTF-8"));
        LOG.info("Registered service:{} to zookeeper.", serviceName);
    }

    /**
     * Make http service url.
     */
    protected String makeServiceUrl(String serviceName) {
        return "http://" + ip + ":" + port + serviceName;
    }
}
