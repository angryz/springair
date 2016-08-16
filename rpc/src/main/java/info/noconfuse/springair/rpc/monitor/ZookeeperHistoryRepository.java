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

import com.fasterxml.jackson.databind.ObjectMapper;
import info.noconfuse.springair.rpc.ZookeeperRegistryClient;
import org.apache.curator.utils.ZKPaths;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence history into zookeeper.
 *
 * @author Zheng Zhipeng
 */
public class ZookeeperHistoryRepository extends ZookeeperRegistryClient implements HistoryRepository {

    protected ZookeeperHistoryRepository(String registryAddress) {
        super(registryAddress);
    }

    protected ZookeeperHistoryRepository(String registryAddress, String nameSpace) {
        super(registryAddress, nameSpace);
    }

    @Override
    public void save(History history) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String path = ZKPaths.makePath(DEFAULT_HISTORY_TOP_PATH, history.getServiceName(),
                format.format(history.getTime()));
        if (getZookeeperClient().checkExists().forPath(path) == null) {
            ObjectMapper mapper = new ObjectMapper();
            byte[] data = mapper.writeValueAsBytes(history);
            getZookeeperClient().create().creatingParentsIfNeeded().forPath(path, data);
        }
    }

    @Override
    public List<History> findAll() throws Exception {
        List<String> serviceNodes = getZookeeperClient().getChildren().forPath
                (DEFAULT_HISTORY_TOP_PATH);
        List<History> historyList = new ArrayList<>(serviceNodes.size() * 100);
        for (String node : serviceNodes) {
            historyList.addAll(findAll(node));
        }
        return historyList;
    }

    @Override
    public List<History> findAll(String serviceName) throws Exception {
        String path = ZKPaths.makePath(DEFAULT_HISTORY_TOP_PATH, serviceName);
        List<String> serviceHistoryNode = getZookeeperClient().getChildren().forPath(path);
        List<History> historyList = new ArrayList<>(serviceHistoryNode.size());
        ObjectMapper mapper = new ObjectMapper();
        for (String node : serviceHistoryNode) {
            String nodePath = ZKPaths.makePath(path, node);
            byte[] data = getZookeeperClient().getData().forPath(nodePath);
            historyList.add(mapper.readValue(data, History.class));
        }
        return historyList;
    }

}
