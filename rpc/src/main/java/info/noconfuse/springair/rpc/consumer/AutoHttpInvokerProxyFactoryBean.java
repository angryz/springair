package info.noconfuse.springair.rpc.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

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
        LOG.info(" ---------- call getServiceUrl() ---------- ");
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

    @Override
    protected RemoteInvocationResult executeRequest(RemoteInvocation invocation) throws Exception {
        try {
            return super.executeRequest(invocation);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            // sleep 200ms then retry once
            Thread.sleep(200);
            return super.executeRequest(invocation);
        }
    }
}
