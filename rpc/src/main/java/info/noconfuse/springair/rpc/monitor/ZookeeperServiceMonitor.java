package info.noconfuse.springair.rpc.monitor;

import info.noconfuse.springair.rpc.ServiceGroup;
import info.noconfuse.springair.rpc.ServiceNode;
import info.noconfuse.springair.rpc.ZookeeperRegistryClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by zzp on 8/11/16.
 */
public class ZookeeperServiceMonitor extends ZookeeperRegistryClient implements ServiceMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperServiceMonitor.class);

    private ConcurrentMap<String, TreeCache> caches;
    private TreeCache nameSpaceTreeCache;

    protected ZookeeperServiceMonitor(String registryAddress) {
        this(registryAddress, null);
    }

    protected ZookeeperServiceMonitor(String registryAddress, String nameSpace) {
        super(registryAddress, nameSpace);
    }

    @Override
    public List<ServiceGroup> allServices() throws Exception {
        List<ServiceGroup> allServices = null;
        if (nameSpaceTreeCache == null) {
            synchronized (ZookeeperServiceMonitor.class) {
                if (nameSpaceTreeCache == null) {
                    nameSpaceTreeCache = TreeCache.newBuilder(getZookeeperClient(), "/").setCacheData(true).build();
                    nameSpaceTreeCache.start();
                    nameSpaceTreeCache.getListenable().addListener(new TreeCacheListener() {
                        @Override
                        public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                            LOG.info("EVENT {}", event.toString());
                        }
                    });
                    List<String> children = getZookeeperClient().getChildren().forPath("/");
                    allServices = new ArrayList<>(children.size());
                    if (children != null && !children.isEmpty()) {
                        LOG.info("All Services : {} ", children.toString());
                        for (String child : children) {
                            List<String> grandChildren = getZookeeperClient().getChildren().forPath("/" + child);
                            List<ServiceNode> serviceNodes = new ArrayList<>(grandChildren.size());
                            for (String grandChild : grandChildren) {
                                LOG.info("Instance of {} : {} ", child, grandChild);
                                String nodePath = ZKPaths.makePath(child, grandChild);
                                String url = new String(getZookeeperClient().getData().forPath(nodePath), "UTF-8");
                                Stat stat = getZookeeperClient().checkExists().forPath(nodePath);
                                serviceNodes.add(ServiceNode.builder().name(grandChild)
                                        .address(url).nodePath(nodePath).ctime(stat.getCtime()).build());
                            }
                            allServices.add(new ServiceGroup(child, serviceNodes));
                        }
                    }
                } else {
                    allServices = allServicesFromCache();
                }
            }
        } else {
            allServices = allServicesFromCache();
        }
        return allServices;
    }

    protected List<ServiceGroup> allServicesFromCache() throws Exception {
        List<ServiceGroup> allServices = null;
        Map<String, ChildData> children = nameSpaceTreeCache.getCurrentChildren("/");
        if (children != null && !children.isEmpty()) {
            LOG.info("All Services : {} ", children.keySet().toString());
            allServices = new ArrayList<>(children.size());
            for (Map.Entry<String, ChildData> entry : children.entrySet()) {
                Map<String, ChildData> grandChildren = nameSpaceTreeCache.getCurrentChildren(entry.getValue().getPath());
                List<ServiceNode> serviceNodes = new ArrayList<>(grandChildren.size());
                for (Map.Entry<String, ChildData> subEntry : grandChildren.entrySet()) {
                    LOG.info("Instance of {} : {} ", entry.getKey(), subEntry.getKey());
                    String url = new String(subEntry.getValue().getData(), "UTF-8");
                    serviceNodes.add(ServiceNode.builder().name(subEntry.getKey())
                            .address(url).nodePath(subEntry.getValue().getPath())
                            .ctime(subEntry.getValue().getStat().getCtime()).build());
                }
                allServices.add(new ServiceGroup(entry.getKey(), serviceNodes));
            }
        }
        return allServices;
    }
}
