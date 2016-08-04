package info.noconfuse.springair.rpc;

/**
 * Created by zzp on 8/4/16.
 */
public class ZookeeperServiceRegistration {

    private final String registryAddress;

    private String nameSpace;

    public ZookeeperServiceRegistration(String registryAddress) {
        this.registryAddress = registryAddress;
    }

}
