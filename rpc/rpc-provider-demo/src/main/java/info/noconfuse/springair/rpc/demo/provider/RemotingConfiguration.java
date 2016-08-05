package info.noconfuse.springair.rpc.demo.provider;

import info.noconfuse.springair.rpc.AutoHttpInvokerServicesExporter;
import info.noconfuse.springair.rpc.LocalHostUtils;
import info.noconfuse.springair.rpc.ServiceRegistration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zzp on 7/29/16.
 */
@Configuration
public class RemotingConfiguration implements ApplicationContextAware {

    private ApplicationContext context;

    /*
    @Bean(name = "/UserService")
    HttpInvokerServiceExporter httpInvokerServiceExporter() {
        String[] beanNames = context.getBeanNamesForAnnotation(RpcService.class);
        for (String beanName : beanNames) {
            System.out.println(">>> " + beanName);
        }
        Map<String, Object> beanMap = context.getBeansWithAnnotation(RpcService.class);
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            System.out.println(">>> " + entry.getKey() + " : " + entry.getValue());
            Class[] classes = entry.getValue().getClass().getInterfaces();
            System.out.println("--- " + classes[0].getName());
            System.out.println("--- " + classes[0].getCanonicalName());
            System.out.println("--- " + classes[0].getTypeName());
            System.out.println("--- " + classes[0].getSimpleName());
        }
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService(beanMap.get("userServiceImpl"));
        exporter.setServiceInterface(UserService.class);
        return exporter;
    }
    */

    @Bean
    AutoHttpInvokerServicesExporter autoHttpInvokerServicesExporter() {
        ServiceRegistration serviceRegistration = new ServiceRegistration() {
            @Override
            public void registerService(String serviceName) {
                System.out.println(">>> http://" + LocalHostUtils.ip() + ":" + LocalHostUtils.serverPort() + serviceName);
            }
        };
        AutoHttpInvokerServicesExporter exporter = new AutoHttpInvokerServicesExporter(serviceRegistration);
        return exporter;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
