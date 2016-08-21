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

package info.noconfuse.springair.rpc.monitor.consumer;

import info.noconfuse.springair.rpc.ConsumerNode;
import info.noconfuse.springair.rpc.ZookeeperRegistryClient;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Consumer monitor implementation with zookeeper.
 *
 * @author Zheng Zhipeng
 */
public class ZookeeperConsumerMonitor extends ZookeeperRegistryClient implements ConsumerMonitor {

    public ZookeeperConsumerMonitor(String registryAddress) {
        super(registryAddress);
    }

    public ZookeeperConsumerMonitor(String registryAddress, String nameSpace) {
        super(registryAddress, nameSpace);
    }

    @Override
    public List<ConsumerNode> allConsumers(String serviceName) throws Exception {
        List<ConsumerNode> result;
        String path = ZKPaths.makePath(DEFAULT_CONSUMERS_TOP_PATH, serviceName);
        if (getZookeeperClient().checkExists().forPath(path) == null)
            result = new ArrayList<>(0);
        else {
            List<String> children = getZookeeperClient().getChildren().forPath(path);
            result = new ArrayList<>(children.size());
            for (String child : children) {
                String childPath = ZKPaths.makePath(path, child);
                Stat stat = getZookeeperClient().checkExists().forPath(childPath);
                String data = new String(getZookeeperClient().getData().forPath(childPath),
                        "UTF-8");
                ConsumerNode consumer = new ConsumerNode();
                consumer.setServiceName(serviceName);
                consumer.setServiceAddress(data);
                consumer.setName(child);
                consumer.setCtime(new Date(stat.getCtime()));
                result.add(consumer);
            }
        }
        return result;
    }
}
