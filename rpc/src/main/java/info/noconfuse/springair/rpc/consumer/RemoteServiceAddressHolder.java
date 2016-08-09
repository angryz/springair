package info.noconfuse.springair.rpc.consumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A Shared container for urls of all services.
 *
 * @author Zheng Zhipeng
 */
public class RemoteServiceAddressHolder {

    private static ConcurrentMap<String, String> holder = new ConcurrentHashMap<>();

    /**
     * Set a url of a service into holder.
     */
    public static void setAddress(String serviceName, String serviceUrl) {
        holder.put(serviceName, serviceUrl);
    }

    /**
     * Get a url of a service from holder.
     */
    public static String getAddress(String serviceName) {
        return holder.get(serviceName);
    }

    /**
     * Check whether a service has an existed url.
     */
    public static boolean isExists(String serviceName) {
        return holder.containsKey(serviceName);
    }

    /**
     * Remove a service from holder.
     */
    public static void remove(String serviceName) {
        holder.remove(serviceName);
    }
}
