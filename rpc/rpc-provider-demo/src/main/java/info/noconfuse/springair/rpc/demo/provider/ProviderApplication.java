package info.noconfuse.springair.rpc.demo.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by zzp on 7/29/16.
 */
@SpringBootApplication
public class ProviderApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ProviderApplication.class, args);
    }
}
