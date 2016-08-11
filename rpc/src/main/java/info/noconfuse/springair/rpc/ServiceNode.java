package info.noconfuse.springair.rpc;

import java.util.Date;

/**
 * @author Zheng Zhipeng
 */
public class ServiceNode {

    private String name;
    private String address;
    private String nodePath;
    private Date ctime;

    public ServiceNode() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public static class Builder {
        private String name;
        private String address;
        private String nodePath;
        private Date ctime;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder nodePath(String path) {
            this.nodePath = path;
            return this;
        }

        public Builder ctime(long ctime) {
            this.ctime = new Date(ctime);
            return this;
        }

        public ServiceNode build() {
            ServiceNode node = new ServiceNode();
            node.setName(name);
            node.setAddress(address);
            node.setNodePath(nodePath);
            node.setCtime(ctime);
            return node;
        }
    }
}
