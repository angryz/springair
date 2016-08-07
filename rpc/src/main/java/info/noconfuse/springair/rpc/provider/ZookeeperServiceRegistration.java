package info.noconfuse.springair.rpc.provider;

import info.noconfuse.springair.rpc.LocalHostUtils;
import info.noconfuse.springair.rpc.ZookeeperRegistryClient;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
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

    private String ip;
    private int port;

    public ZookeeperServiceRegistration(String registryAddress) {
        this(registryAddress, null);
    }

    public ZookeeperServiceRegistration(String registryAddress, String nameSpace) {
        super(registryAddress, nameSpace);
        ip = LocalHostUtils.ip();
        port = LocalHostUtils.serverPort();
        Assert.isTrue(port > 0, "Can not find local server port config 'local.server.port' or 'tomcat.server.port'");
    }

    @Override
    public void registerService(String serviceName) throws Exception {
        String url = makeServiceUrl(serviceName);
        // create ephemral node for service instance
        getZookeeperClient().create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(ZKPaths.makePath(serviceName, ip + ":" + port), url.getBytes("UTF-8"));
        LOG.info("Registered service:{} to zookeeper.", serviceName);
    }

    /**
     * Make http service url.
     */
    protected String makeServiceUrl(String serviceName) {
        return "http://" + ip + ":" + port + serviceName;
    }
}
