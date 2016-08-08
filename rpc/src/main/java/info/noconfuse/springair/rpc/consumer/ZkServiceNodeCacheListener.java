package info.noconfuse.springair.rpc.consumer;

import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle {@code PathChildrenCacheEvent} of service node.
 * <p>
 * If service node is removed from zookeeper, get a new node when next
 *
 * @author Zheng Zhipeng
 */
public class ZkServiceNodeCacheListener implements NodeCacheListener {

    private static final Logger LOG = LoggerFactory.getLogger(ZkServiceNodeCacheListener.class);

    @Override
    public void nodeChanged() throws Exception {
        LOG.info("Node changed");
    }

}
