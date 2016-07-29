package info.noconfuse.springair.rpc.demo.provider;

import info.noconfuse.springair.rpc.demo.client.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 * Created by zzp on 7/29/16.
 */
@Configuration
public class RemotingConfiguration {

    @Autowired
    private UserService userService;

    @Bean(name = "/userService")
    HttpInvokerServiceExporter httpInvokerServiceExporter() {
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService(userService);
        exporter.setServiceInterface(UserService.class);
        return exporter;
    }
}
