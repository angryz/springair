package info.noconfuse.springair.rpc.consumer;

import info.noconfuse.springair.rpc.ZookeeperRegistryClient;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private ConcurrentMap<String, PathChildrenCache> caches;

    public ZookeeperServiceDiscovery(String registryAddress) {
        this(registryAddress, null);
    }

    public ZookeeperServiceDiscovery(String registryAddress, String nameSpace) {
        super(registryAddress, nameSpace);
        caches = new ConcurrentHashMap<>();
    }

    @Override
    public String getServiceAddress(String serviceName) throws Exception {
        PathChildrenCache serviceNodeCache = caches.get(serviceName);
        if (serviceNodeCache == null) {
            // check serviceName exists
            if (getZookeeperClient().checkExists().forPath(serviceName) == null)
                throw new NoSuchElementException("No service named as '" + serviceName + "' registered");
            // cache serviceName node, and listen for changing
            serviceNodeCache = new PathChildrenCache(getZookeeperClient(), serviceName, true);
            serviceNodeCache.start();
            serviceNodeCache.getListenable().addListener(new ZkServiceNodeCacheListener(serviceName, serviceNodeCache));
            serviceNodeCache.rebuild();
            caches.put(serviceName, serviceNodeCache);
        }
        // get a random service instance address
        List<ChildData> children = serviceNodeCache.getCurrentData();
        int choice = new Random().nextInt(children.size());
        String url = new String(children.get(choice).getData(), "UTF-8");
        LOG.info("Got service url : {}", url);
        RemoteServiceAddressHolder.setAddress(serviceName, url);
        return url;
    }
}
