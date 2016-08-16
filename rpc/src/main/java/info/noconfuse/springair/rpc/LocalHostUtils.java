/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Zheng Zhipeng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
