package info.noconfuse.springair.rpc;

import com.sun.corba.se.spi.activation.Server;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by zzp on 8/4/16.
 */
public class ZookeeperServiceRegistration implements ServiceRegistration {

    private final String registryAddress;

    private String nameSpace;

    private String ip;
    private int port;

    public ZookeeperServiceRegistration(String registryAddress) {
        this.registryAddress = registryAddress;
        ip = LocalHostUtils.ip();
        port = LocalHostUtils.serverPort();
        Assert.isTrue(port > 0, "Can not find local server port config 'local.server.port' or 'tomcat.server.port'");
    }

    @PostConstruct
    public void connectZookeeper() {

    }

    @PreDestroy
    public void disconnectZookeeper() {

    }

    @Override
    public void registerService(String serviceName) {
        String url = makeServiceUrl(serviceName);

    }

    protected String makeServiceUrl(String serviceName) {
        return "http://" + ip + ":" + port + serviceName;
    }
}
