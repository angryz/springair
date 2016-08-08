package info.noconfuse.springair.rpc.consumer;

import info.noconfuse.springair.rpc.ZookeeperRegistryClient;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.utils.ZKPaths;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

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
        if (getZookeeperClient().checkExists().forPath(serviceName) == null)
            throw new NoSuchElementException("No service named as '" + serviceName + "' registered");
        NodeCache serviceNodeCache = new NodeCache(getZookeeperClient(),
                ZKPaths.makePath(getNameSpace(), serviceName));
        serviceNodeCache.start();
        serviceNodeCache.getListenable().addListener(new ZkServiceNodeCacheListener());
        List<String> instanceList = getZookeeperClient().getChildren().forPath(serviceName);
        if (instanceList == null || instanceList.isEmpty())
            throw new NoSuchElementException("No instance of '" + serviceName + "' registered");
        int choice = new Random().nextInt(instanceList.size());
        String path = ZKPaths.makePath(serviceName, instanceList.get(choice));
        return new String(getZookeeperClient().getData().forPath(path), "UTF-8");
    }
}
