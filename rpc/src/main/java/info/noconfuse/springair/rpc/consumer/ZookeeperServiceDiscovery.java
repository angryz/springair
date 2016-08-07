package info.noconfuse.springair.rpc.consumer;

import info.noconfuse.springair.rpc.ZookeeperRegistryClient;

import java.util.NoSuchElementException;

/**
 * Use zookeeper as registry of services.
 * Connect to zookeeper and find service instance by the name.
 * <p>
 * Watch the service registry, refetch service instance if current
 * one is offline.
 *
 * @author Zheng Zhipeng
 */
public class ZookeeperServiceDiscovery extends ZookeeperRegistryClient implements ServiceDiscovery {

    public ZookeeperServiceDiscovery(String registryAddress) {
        this(registryAddress, null);
    }

    public ZookeeperServiceDiscovery(String registryAddress, String nameSpace) {
        super(registryAddress, nameSpace);
    }

    @Override
    public String getServiceAddress(String serviceName) throws Exception {
        String path = makeNodePath(serviceName, "127.0.0.1:8080");
        if (getZookeeperClient().checkExists().forPath(path) == null)
            throw new NoSuchElementException("Cannot find path '" + path + "' in zookeeper");
        return new String(getZookeeperClient().getData().forPath(path), "UTF-8");
    }
}
