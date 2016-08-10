package info.noconfuse.springair.rpc;

import java.util.List;

/**
 * @author Zheng Zhipeng
 */
public class ServiceGroup {

    private String name;
    private List<ServiceNode> children;

    public ServiceGroup() {
    }

    public ServiceGroup(String name, List<ServiceNode> children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ServiceNode> getChildren() {
        return children;
    }

    public void setChildren(List<ServiceNode> children) {
        this.children = children;
    }
}
