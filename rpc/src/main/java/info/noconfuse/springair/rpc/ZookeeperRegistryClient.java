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

    protected static final String DEFAULT_NAMESPACE = "services_registry";

    protected ZookeeperRegistryClient(String registryAddress) {
        this(registryAddress, null);
    }

    protected ZookeeperRegistryClient(String registryAddress, String nameSpace) {
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
    protected void createEphemeralNode(String path) throws Exception {
        getZookeeperClient().create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path);
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    protected CuratorFramework getZookeeperClient() {
        return zookeeperClient;
    }
}
