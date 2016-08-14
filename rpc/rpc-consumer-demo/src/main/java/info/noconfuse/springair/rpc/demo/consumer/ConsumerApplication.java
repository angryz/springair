package info.noconfuse.springair.rpc.demo.consumer;

import info.noconfuse.springair.rpc.demo.client.User;
import info.noconfuse.springair.rpc.demo.client.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

/**
 */
@SpringBootApplication
public class ConsumerApplication implements CommandLineRunner {

    @Autowired
    HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean;

    @Override
    public void run(String... args) throws Exception {
        UserService userService = (UserService) httpInvokerProxyFactoryBean.getObject();
        long endTime = System.currentTimeMillis() + (2 * 60 * 1000);//run 2 minutes
        while (System.currentTimeMillis() < endTime) {
            String results = userService.hello(new User("Peter", "Jackson"));
            System.out.println(System.currentTimeMillis() + " | " + results);
            Thread.sleep(1000);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
