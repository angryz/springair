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

package info.noconfuse.springair.rpc.consumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A Shared container for urls of all services.
 *
 * @author Zheng Zhipeng
 */
public class RemoteServiceAddressHolder {

    private static ConcurrentMap<String, String> holder = new ConcurrentHashMap<>();

    /**
     * Set a url of a service into holder.
     */
    public static void setAddress(String serviceName, String serviceUrl) {
        holder.put(serviceName, serviceUrl);
    }

    /**
     * Get a url of a service from holder.
     */
    public static String getAddress(String serviceName) {
        return holder.get(serviceName);
    }

    /**
     * Check whether a service has an existed url.
     */
    public static boolean isExists(String serviceName) {
        return holder.containsKey(serviceName);
    }

    /**
     * Remove a service from holder.
     */
    public static void remove(String serviceName) {
        holder.remove(serviceName);
    }
}
