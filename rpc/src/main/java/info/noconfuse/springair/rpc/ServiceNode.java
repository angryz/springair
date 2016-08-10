package info.noconfuse.springair.rpc;

/**
 * @author Zheng Zhipeng
 */
public class ServiceNode {

    private String name;
    private String url;
    private String nodePath;

    public ServiceNode() {
    }

    public ServiceNode(String name, String url, String nodePath) {
        this.name = name;
        this.url = url;
        this.nodePath = nodePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }
}
