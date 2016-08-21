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

package info.noconfuse.springair.rpc.monitor.history;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.noconfuse.springair.rpc.ZookeeperRegistryClient;
import org.apache.curator.utils.ZKPaths;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static info.noconfuse.springair.rpc.monitor.history.ScheduledHistoryCleaner
        .MAX_HISTORY_LIMIT_PER_SERV;

/**
 * Persistence history into zookeeper.
 *
 * @author Zheng Zhipeng
 */
public class ZookeeperHistoryRepository extends ZookeeperRegistryClient implements HistoryRepository {

    private ConcurrentMap<String, List<String>> historyCache = new ConcurrentHashMap<>();

    @Autowired
    ScheduledHistoryCleaner scheduledHistoryCleaner;

    public ZookeeperHistoryRepository(String registryAddress) {
        super(registryAddress);
    }

    public ZookeeperHistoryRepository(String registryAddress, String nameSpace) {
        super(registryAddress, nameSpace);
    }

    @Override
    public void save(History history) throws Exception {
        String path = ZKPaths.makePath(DEFAULT_HISTORY_TOP_PATH, history.getServiceName(),
                makeHistoryNodeName(history));
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
        historyList.sort(new Comparator<History>() {
            @Override
            public int compare(History o1, History o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });
        return historyList;
    }

    @Override
    public List<History> findAll(String serviceName) throws Exception {
        String path = ZKPaths.makePath(DEFAULT_HISTORY_TOP_PATH, serviceName);
        List<String> serviceHistoryNode = getZookeeperClient().getChildren().forPath(path);
        serviceHistoryNode.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
        List<String> abandoned = serviceHistoryNode.size() > MAX_HISTORY_LIMIT_PER_SERV ?
                serviceHistoryNode.subList(MAX_HISTORY_LIMIT_PER_SERV - 1,
                        serviceHistoryNode.size() - 1)
                : new ArrayList<>(0);
        //List<String> cachedServiceHistory = getCachedServiceHistory(serviceName);
        List<History> historyList = new ArrayList<>(serviceHistoryNode.size());
        ObjectMapper mapper = new ObjectMapper();
        for (String node : serviceHistoryNode) {
            String nodePath = ZKPaths.makePath(path, node);
            byte[] data = getZookeeperClient().getData().forPath(nodePath);
            History history = mapper.readValue(data, History.class);
            historyList.add(history);
            // abandon extra history nodes
            if (!abandoned.isEmpty() && abandoned.contains(node))
                scheduledHistoryCleaner.abandon(history);

        }
        return historyList;
    }

    @Override
    public void remove(History history) throws Exception {
        String path = ZKPaths.makePath(DEFAULT_HISTORY_TOP_PATH, history.getServiceName(),
                makeHistoryNodeName(history));
        getZookeeperClient().delete().deletingChildrenIfNeeded().inBackground().forPath(path);
    }

    protected List<String> getCachedServiceHistory(String serviceName) {
        List<String> cachedServiceHistory = historyCache.get(serviceName);
        return cachedServiceHistory == null ? new ArrayList<>(50) : cachedServiceHistory;
    }

    private String makeHistoryNodeName(History history) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return format.format(history.getTime());
    }
}
