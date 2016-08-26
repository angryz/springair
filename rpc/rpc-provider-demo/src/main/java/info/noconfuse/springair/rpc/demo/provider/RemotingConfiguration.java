package info.noconfuse.springair.rpc.demo.provider;

import info.noconfuse.springair.rpc.provider.AutoHttpInvokerServicesExporter;
import info.noconfuse.springair.rpc.provider.ZookeeperServiceRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zzp on 7/29/16.
 */
@Configuration
public class RemotingConfiguration {

    @Bean
    ZookeeperServiceRegistration zookeeperServiceRegistration() {
        return new ZookeeperServiceRegistration("127.0.0.1:2181");
    }

    @Bean
    AutoHttpInvokerServicesExporter autoHttpInvokerServicesExporter(ZookeeperServiceRegistration serviceRegistration) {
        AutoHttpInvokerServicesExporter exporter = new AutoHttpInvokerServicesExporter(serviceRegistration);
        return exporter;
    }
}
