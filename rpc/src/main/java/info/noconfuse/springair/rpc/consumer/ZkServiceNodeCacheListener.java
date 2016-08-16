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

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static info.noconfuse.springair.rpc.consumer.RemoteServiceAddressHolder.getAddress;
import static info.noconfuse.springair.rpc.consumer.RemoteServiceAddressHolder.remove;

/**
 * Handle {@code PathChildrenCacheEvent} of service node.
 * <p>
 * If service node is removed from zookeeper, get a new node when next
 *
 * @author Zheng Zhipeng
 */
public class ZkServiceNodeCacheListener implements PathChildrenCacheListener {

    private static final Logger LOG = LoggerFactory.getLogger(ZkServiceNodeCacheListener.class);

    private final String serviceName;
    private final PathChildrenCache cache;

    public ZkServiceNodeCacheListener(String serviceName, PathChildrenCache cache) {
        this.serviceName = serviceName;
        this.cache = cache;
    }

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        if (event.getData() == null)
            return;
        String nodeValue = new String(event.getData().getData(), "UTF-8");
        switch (event.getType()) {
            case CHILD_REMOVED:
                // service instance used by this consumer offline
                if (nodeValue.equals(getAddress(serviceName))) {
                    remove(serviceName);
                }
                break;
            default:
                LOG.info("Service [{}] {}", serviceName, event.toString());
        }
    }

}
