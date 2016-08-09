package info.noconfuse.springair.rpc.consumer;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static info.noconfuse.springair.rpc.consumer.RemoteServiceAddressHolder.*;

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
        String nodePath = event.getData().getPath();
        String nodeValue = new String(event.getData().getData(), "UTF-8");
        switch (event.getType()) {
            case CHILD_REMOVED:
                // service instance used by this consumer offline
                if (nodePath.equals(getAddress(serviceName))) {
                    remove(serviceName);
                }
                break;
            case CHILD_UPDATED:
                // service instance used by this consumer changed address
                if (nodePath.equals(getAddress(serviceName))) {
                    setAddress(serviceName, nodeValue);
                }
                break;
            default:
                LOG.info("Service [{}] {}", serviceName, event.toString());
        }
    }

}
