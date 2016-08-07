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
    private String serviceUrl;

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
        if (serviceUrl == null) {
            synchronized (AutoHttpInvokerProxyFactoryBean.class) {
                if (serviceUrl == null) {
                    try {
                        serviceUrl = serviceDiscovery.getServiceAddress(serviceName);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
            LOG.info("Got service url : {}", serviceUrl);
        }
        return serviceUrl;
    }
}
