package info.noconfuse.springair.rpc;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Local host utilities.
 *
 * @author Zheng Zhipeng
 */
public class LocalHostUtils {

    /**
     * Get localhost hostname.
     */
    public static String hostName() {
        String hostName = System.getProperty("server.name");
        if (hostName != null)
            return hostName;
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * Get localhost ip address.
     */
    public static String ip() {
        String ip = System.getProperty("server.ip");
        if (ip != null)
            return ip;
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * Get configured local server port.
     */
    public static int serverPort() {
        int portNum = -1;
        String port = System.getProperty("server.port");
        port = port == null ? System.getProperty("tomcat.server.port") : port;
        if (port != null) {
            try {
                portNum = Integer.parseInt(port);
            } catch (NumberFormatException e) {
            }
        }
        return portNum;
    }
}
