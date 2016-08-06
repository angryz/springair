package info.noconfuse.springair.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * Auto export services which annotated with @RpcService.
 *
 * @author Zheng Zhipeng
 */
public class AutoHttpInvokerServicesExporter implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(AutoHttpInvokerServicesExporter.class);

    private ApplicationContext applicationContext;
    private ConfigurableListableBeanFactory beanFactory;

    private final ServiceRegistration serviceRegistration;

    public AutoHttpInvokerServicesExporter(ServiceRegistration serviceRegistration) {
        this.serviceRegistration = serviceRegistration;
    }

    @PostConstruct
    protected void registerServices() throws Exception {
        // find beans which annotated with @RpcService
        Map<String, Object> serviceBeanMaps = applicationContext.getBeansWithAnnotation(RpcService.class);
        LOG.debug("Find {} beans annotated with @RpcService", serviceBeanMaps.size());
        for (Map.Entry<String, Object> entry : serviceBeanMaps.entrySet()) {
            Object serviceImpl = entry.getValue();
            Class[] interfaces = serviceImpl.getClass().getInterfaces();
            // create HttpInvokerServiceExporter instance for each bean
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
            exporter.setService(serviceImpl);
            exporter.setServiceInterface(interfaces[0]);
            String exportedName = "/" + interfaces[0].getSimpleName();
            LOG.info("Registering RPC service '{}'", exportedName);
            beanFactory.registerSingleton(exportedName, exporter);
            beanFactory.initializeBean(exporter, exportedName);
            serviceRegistration.registerService(exportedName);
        }
    }

    @PreDestroy
    protected void cleanupServices() {
        LOG.info("Clean up RPC services");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
    }
}
