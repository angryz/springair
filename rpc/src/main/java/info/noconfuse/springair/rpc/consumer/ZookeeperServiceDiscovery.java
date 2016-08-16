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

package info.noconfuse.springair.rpc.consumer;

import info.noconfuse.springair.rpc.ZookeeperRegistryClient;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.utils.ZKPaths;
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
        String servicePath = ZKPaths.makePath(DEFAULT_SERVICES_TOP_PATH, serviceName);
        PathChildrenCache serviceNodeCache = caches.get(serviceName);
        if (serviceNodeCache == null) {
            // check serviceName exists
            if (getZookeeperClient().checkExists().forPath(servicePath) == null)
                throw new NoSuchElementException("No service named as '" + serviceName + "' registered");
            // cache serviceName node, and listen for changing
            serviceNodeCache = new PathChildrenCache(getZookeeperClient(), servicePath, true);
            serviceNodeCache.start();
            serviceNodeCache.getListenable().addListener(new ZkServiceNodeCacheListener(servicePath, serviceNodeCache));
            serviceNodeCache.rebuild();
            caches.put(serviceName, serviceNodeCache);
        }
        // get a random service instance address
        List<ChildData> children = serviceNodeCache.getCurrentData();
        int choice = new Random().nextInt(children.size());
        String url = new String(children.get(choice).getData(), "UTF-8");
        LOG.info("Got address of '{}' : {}", serviceName, url);
        RemoteServiceAddressHolder.setAddress(serviceName, url);
        // TODO register consumer at registry asynchronizely
        return url;
    }
}
