package info.noconfuse.springair.rpc.consumer;

/**
 * Discover a service provider from Registry Center.
 *
 * @author Zheng Zhipeng
 */
public interface ServiceDiscovery {

    /**
     * Get a specified service address from service retistry.
     */
    String getServiceAddress(String serviceName) throws Exception;
}
