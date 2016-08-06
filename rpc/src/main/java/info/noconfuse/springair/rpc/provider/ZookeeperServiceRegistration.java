package info.noconfuse.springair.rpc.provider;

import info.noconfuse.springair.rpc.LocalHostUtils;
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
 * Use zookeeper as service registry. Connect to zookeeper then create nodes for services.
 *
 * @author Zheng Zhipeng
 */
public class ZookeeperServiceRegistration implements ServiceRegistration {

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperServiceRegistration.class);

    private final String registryAddress;

    private CuratorFramework zookeeperClient;

    private String nameSpace = "services_registry";

    private String ip;
    private int port;

    public ZookeeperServiceRegistration(String registryAddress) {
        this.registryAddress = registryAddress;
        ip = LocalHostUtils.ip();
        port = LocalHostUtils.serverPort();
        Assert.isTrue(port > 0, "Can not find local server port config 'local.server.port' or 'tomcat.server.port'");
    }

    @PostConstruct
    public void connectZookeeper() throws Exception {
        if (zookeeperClient == null) {
            synchronized (ZookeeperServiceRegistration.class) {
                if (zookeeperClient == null) {
                    RetryPolicy retryPolicy = new BoundedExponentialBackoffRetry(500, 3000, 5);
                    zookeeperClient = CuratorFrameworkFactory.builder()
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
        if (zookeeperClient.checkExists().forPath("/" + nameSpace) == null)
            zookeeperClient.create().forPath("/" + nameSpace);
    }

    @PreDestroy
    public void disconnectZookeeper() {
        if (zookeeperClient != null) {
            zookeeperClient.close();
            LOG.info("Disonnected to zookeeper server.");
        }
    }

    @Override
    public void registerService(String serviceName) throws Exception {
        String url = makeServiceUrl(serviceName);
        // create ephemral node for service instance
        zookeeperClient.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(makeNodePath(serviceName, ip), url.getBytes("UTF-8"));
        LOG.info("Registered service:{} to zookeeper.", serviceName);
    }

    /**
     * Make http service url.
     */
    protected String makeServiceUrl(String serviceName) {
        return "http://" + ip + ":" + port + serviceName;
    }

    /**
     * Make zookeeper node path with node names.
     */
    protected String makeNodePath(String... nodes) {
        StringBuilder sb = new StringBuilder("/");
        sb.append(nameSpace);
        for (String node : nodes) {
            if (!node.startsWith("/"))
                sb.append("/");
            sb.append(node);
        }
        return sb.toString();
    }
}
