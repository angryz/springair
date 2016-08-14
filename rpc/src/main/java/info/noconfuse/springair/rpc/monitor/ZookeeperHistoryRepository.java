package info.noconfuse.springair.rpc.monitor;

import info.noconfuse.springair.rpc.ZookeeperRegistryClient;

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
    public void save(History history) {

    }

    @Override
    public History findAll() {
        return null;
    }
}
