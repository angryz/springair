package info.noconfuse.springair.rpc.monitor;

import java.util.Date;

/**
 * @author Zheng Zhipeng
 */
public class History {

    private Date time;
    private String serviceName;
    private String serviceInstanceName;
    private Action action;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceInstanceName() {
        return serviceInstanceName;
    }

    public void setServiceInstanceName(String serviceInstanceName) {
        this.serviceInstanceName = serviceInstanceName;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public enum Action {
        ONLINE, OFFLINE;
    }
}
