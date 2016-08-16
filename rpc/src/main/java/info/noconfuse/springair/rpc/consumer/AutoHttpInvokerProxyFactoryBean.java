/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Zheng Zhipeng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
