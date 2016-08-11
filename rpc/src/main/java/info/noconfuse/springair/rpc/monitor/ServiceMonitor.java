package info.noconfuse.springair.rpc.monitor;

import info.noconfuse.springair.rpc.ServiceGroup;

import java.util.List;

/**
 * Monitor all services which registered at registry.
 *
 * @author Zheng Zhipeng
 */
public interface ServiceMonitor {

    /**
     * Get all services from registry.
     */
    List<ServiceGroup> allServices() throws Exception;

}
