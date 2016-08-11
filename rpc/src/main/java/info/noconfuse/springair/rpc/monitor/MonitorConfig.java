package info.noconfuse.springair.rpc.monitor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zheng Zhipeng
 */
@Configuration
public class MonitorConfig {

    @Bean
    ServiceMonitor serviceMonitor() {
        return new ZookeeperServiceMonitor("127.0.0.1:2181");
    }
}
