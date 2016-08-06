package info.noconfuse.springair.rpc.demo.provider;

import info.noconfuse.springair.rpc.provider.AutoHttpInvokerServicesExporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 * Created by zzp on 7/29/16.
 */
@SpringBootApplication
public class ProviderApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ProviderApplication.class, args);
        String[] beanNames = context.getBeanNamesForType(AutoHttpInvokerServicesExporter.class);
        for (String beanName : beanNames) {
            System.out.println(">>> " + beanName);
        }
        beanNames = context.getBeanNamesForType(HttpInvokerServiceExporter.class);
        for (String beanName : beanNames) {
            System.out.println(">>> " + beanName);
        }
    }
}
