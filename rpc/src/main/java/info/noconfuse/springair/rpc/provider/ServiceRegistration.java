package info.noconfuse.springair.rpc.provider;

/**
 * Register a service provider to Registry Center.
 *
 * @author Zheng Zhipeng
 */
public interface ServiceRegistration {

    void registerService(String serviceName) throws Exception;
}
