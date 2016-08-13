package info.noconfuse.springair.rpc.monitor;

/**
 * @author Zheng Zhipeng
 */
public interface HistoryRepository {

    void save(History history);

    History findAll();

}
