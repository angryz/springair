package info.noconfuse.springair.rpc.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

/**
 * Instead of specify a service url, this class use ServiceDiscovery
 * to get service url form service registry.
 *
 * @author Zheng Zhipeng
 */
public class AutoHttpInvokerProxyFactoryBean extends HttpInvokerProxyFactoryBean {

    private static final Logger LOG = LoggerFactory.getLogger(AutoHttpInvokerProxyFactoryBean.class);

    private final ServiceDiscovery serviceDiscovery;
    private String serviceName;

    public AutoHttpInvokerProxyFactoryBean(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public void afterPropertiesSet() {
        this.serviceName = "/" + getServiceInterface().getSimpleName();
        super.afterPropertiesSet();
    }

    @Override
    public String getServiceUrl() {
        if (!RemoteServiceAddressHolder.isExists(serviceName)) {
            synchronized (AutoHttpInvokerProxyFactoryBean.class) {
                if (!RemoteServiceAddressHolder.isExists(serviceName)) {
                    try {
                        return serviceDiscovery.getServiceAddress(serviceName);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        return RemoteServiceAddressHolder.getAddress(serviceName);
    }
}
