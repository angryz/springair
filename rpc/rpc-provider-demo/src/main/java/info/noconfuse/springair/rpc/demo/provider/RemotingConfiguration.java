package info.noconfuse.springair.rpc.demo.provider;

import info.noconfuse.springair.rpc.RpcService;
import info.noconfuse.springair.rpc.demo.client.UserService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Created by zzp on 7/29/16.
 */
@Configuration
public class RemotingConfiguration implements ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    private UserService userService;

    @Bean(name = "/userService")
    HttpInvokerServiceExporter httpInvokerServiceExporter() {
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService(userService);
        exporter.setServiceInterface(UserService.class);
        String[] beanNames = context.getBeanNamesForAnnotation(RpcService.class);
        for (String beanName : beanNames) {
            System.out.println(">>> " + beanName);
        }
        Map<String, Object> beanMap = context.getBeansWithAnnotation(RpcService.class);
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            System.out.println(">>> " + entry.getKey() + " : " + entry.getValue());
            Class[] classes = entry.getValue().getClass().getInterfaces();
            System.out.println("--- " + classes[0]);
        }
        return exporter;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
