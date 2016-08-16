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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Service Monitor implementation with zookeeper.
 * <p>
 * Created by zzp on 8/11/16.
 */
public class ZookeeperServiceMonitor extends ZookeeperRegistryClient implements ServiceMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperServiceMonitor.class);

    @Autowired
    HistoryRepository historyRepository;

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
                    nameSpaceTreeCache.getListenable().addListener(new TreeNodeListener());
                    List<String> children = getZookeeperClient().getChildren().forPath(DEFAULT_SERVICES_TOP_PATH);
                    allServices = new ArrayList<>(children.size());
                    if (children != null && !children.isEmpty()) {
                        LOG.info("All Services : {} ", children.toString());
                        for (String child : children) {
                            String childPath = ZKPaths.makePath(DEFAULT_SERVICES_TOP_PATH, child);
                            List<String> grandChildren = getZookeeperClient().getChildren().forPath(childPath);
                            List<ServiceNode> serviceNodes = new ArrayList<>(grandChildren.size());
                            for (String grandChild : grandChildren) {
                                LOG.info("Instance of {} : {} ", child, grandChild);
                                String nodePath = ZKPaths.makePath(DEFAULT_SERVICES_TOP_PATH, child, grandChild);
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
        Map<String, ChildData> children = nameSpaceTreeCache.getCurrentChildren(DEFAULT_SERVICES_TOP_PATH);
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

    private class TreeNodeListener implements TreeCacheListener {

        @Override
        public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
            LOG.info("EVENT {}", event.toString());
            String path = event.getData() == null ? null : event.getData().getPath();
            String[] nodes = new String[0];
            if (path != null) {
                nodes = path.split("/");
            }
            if (nodes.length == 4 && ("/" + nodes[1]).equals(DEFAULT_SERVICES_TOP_PATH)) {
                switch (event.getType()) {
                    case NODE_ADDED:
                        LOG.info("Service instance {} online.", nodes[3]);
                        History onlineHistory = new History();
                        onlineHistory.setAction(History.Action.ONLINE);
                        onlineHistory.setTime(new Date(event.getData().getStat().getCtime()));
                        onlineHistory.setServiceName(nodes[2]);
                        onlineHistory.setServiceInstanceName(nodes[3]);
                        historyRepository.save(onlineHistory);
                        break;
                    case NODE_REMOVED:
                        LOG.info("Service instance {} offline.", nodes[3]);
                        History offlineHistory = new History();
                        offlineHistory.setAction(History.Action.OFFLINE);
                        offlineHistory.setTime(new Date());
                        offlineHistory.setServiceName(nodes[2]);
                        offlineHistory.setServiceInstanceName(nodes[3]);
                        historyRepository.save(offlineHistory);
                        break;
                }
            }
        }
    }

}
