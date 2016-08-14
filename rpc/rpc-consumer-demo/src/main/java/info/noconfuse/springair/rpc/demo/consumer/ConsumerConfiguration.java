package info.noconfuse.springair.rpc.demo.consumer;

import info.noconfuse.springair.rpc.consumer.AutoHttpInvokerProxyFactoryBean;
import info.noconfuse.springair.rpc.consumer.ZookeeperServiceDiscovery;
import info.noconfuse.springair.rpc.demo.client.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 */
@Configuration
public class ConsumerConfiguration {

    /*
    @Bean
    HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean() {
        HttpInvokerProxyFactoryBean factoryBean = new HttpInvokerProxyFactoryBean();
        factoryBean.setServiceUrl("http://localhost:8080/UserService");
        factoryBean.setServiceInterface(UserService.class);
        return factoryBean;
    }
    */

    @Bean
    ZookeeperServiceDiscovery zookeeperServiceDiscovery() {
        return new ZookeeperServiceDiscovery("127.0.0.1:2181");
    }

    @Bean
    AutoHttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean(ZookeeperServiceDiscovery serviceDiscovery) {
        AutoHttpInvokerProxyFactoryBean factoryBean = new AutoHttpInvokerProxyFactoryBean(serviceDiscovery);
        factoryBean.setServiceInterface(UserService.class);
        return factoryBean;
    }
}
