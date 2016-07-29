package info.noconfuse.springair.rpc.demo.consumer;

import info.noconfuse.springair.rpc.demo.client.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

/**
 * Created by zzp on 7/29/16.
 */
@Configuration
public class ConsumerConfiguration {

    @Bean
    HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean() {
        HttpInvokerProxyFactoryBean factoryBean = new HttpInvokerProxyFactoryBean();
        factoryBean.setServiceUrl("http://localhost:8080/userService");
        factoryBean.setServiceInterface(UserService.class);
        return factoryBean;
    }
}
