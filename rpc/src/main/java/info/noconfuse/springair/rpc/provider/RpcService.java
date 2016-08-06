package info.noconfuse.springair.rpc.provider;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Indicates that an annotated class is a "RpcService".
 * Such classes can be auto-detected by Spring Framework.
 * And will be exposed as Remoting Interfaces.
 *
 * @author Zheng Zhipeng
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RpcService {
}
