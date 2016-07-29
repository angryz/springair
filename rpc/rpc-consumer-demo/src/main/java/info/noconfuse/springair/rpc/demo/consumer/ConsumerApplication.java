package info.noconfuse.springair.rpc.demo.consumer;

import info.noconfuse.springair.rpc.demo.client.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

/**
 * Created by zzp on 7/29/16.
 */
@SpringBootApplication
public class ConsumerApplication implements CommandLineRunner {

    @Autowired
    HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean;

    @Override
    public void run(String... args) throws Exception {
        UserService userService = (UserService) httpInvokerProxyFactoryBean.getObject();
        String results = userService.hello("Raymond");
        System.out.println(results);
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
