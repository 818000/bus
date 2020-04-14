/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.core.utils;

import org.aoju.bus.core.lang.Filter;
import org.aoju.bus.core.lang.RegEx;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.Validator;
import org.aoju.bus.core.lang.exception.InstrumentException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * 网络相关工具
 *
 * @author Kimi Liu
 * @version 5.8.6
 * @since JDK 1.8+
 */
public class NetUtils {

    public static final String LOCAL_IP = "127.0.0.1";

    /**
     * 默认最小端口,1024
     */
    public static final int PORT_RANGE_MIN = 1024;
    /**
     * 默认最大端口,65535
     */
    public static final int PORT_RANGE_MAX = 0xFFFF;

    /**
     * 根据long值获取ip v4地址
     *
     * @param longIP IP的long表示形式
     * @return IP V4 地址
     */
    public static String longToIpv4(long longIP) {
        final StringBuilder sb = new StringBuilder();
        // 直接右移24位
        sb.append((longIP >>> 24));
        sb.append(Symbol.DOT);
        // 将高8位置0,然后右移16位
        sb.append(((longIP & 0x00FFFFFF) >>> 16));
        sb.append(Symbol.DOT);
        sb.append(((longIP & 0x0000FFFF) >>> 8));
        sb.append(Symbol.DOT);
        sb.append((longIP & 0x000000FF));
        return sb.toString();
    }

    /**
     * 根据ip地址计算出long型的数据
     *
     * @param strIP IP V4 地址
     * @return long值
     */
    public static long ipv4ToLong(String strIP) {
        if (Validator.isIpv4(strIP)) {
            long[] ip = new long[4];
            // 先找到IP地址字符串中.的位置
            int position1 = strIP.indexOf(Symbol.DOT);
            int position2 = strIP.indexOf(Symbol.DOT, position1 + 1);
            int position3 = strIP.indexOf(Symbol.DOT, position2 + 1);
            // 将每个.之间的字符串转换成整型
            ip[0] = Long.parseLong(strIP.substring(0, position1));
            ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
            ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
            ip[3] = Long.parseLong(strIP.substring(position3 + 1));
            return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
        }
        return 0;
    }

    /**
     * 检测本地端口可用性
     * 来自org.springframework.util.SocketUtils
     *
     * @param port 被检测的端口
     * @return 是否可用
     */
    public static boolean isUsableLocalPort(int port) {
        if (false == isValidPort(port)) {
            // 给定的IP未在指定端口范围中
            return false;
        }

        // 绑定非127.0.0.1的端口无法被检测到
        try (ServerSocket ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
        } catch (IOException ignored) {
            return false;
        }

        try (DatagramSocket ds = new DatagramSocket(port)) {
            ds.setReuseAddress(true);
        } catch (IOException ignored) {
            return false;
        }

        return false;
    }

    /**
     * 是否为有效的端口
     * 此方法并不检查端口是否被占用
     *
     * @param port 端口号
     * @return 是否有效
     */
    public static boolean isValidPort(int port) {
        // 有效端口是0～65535
        return port >= 0 && port <= PORT_RANGE_MAX;
    }

    /**
     * 查找1024~65535范围内的可用端口
     * 此方法只检测给定范围内的随机一个端口,检测65535-1024次
     * 来自org.springframework.util.SocketUtils
     *
     * @return 可用的端口
     */
    public static int getUsableLocalPort() {
        return getUsableLocalPort(PORT_RANGE_MIN);
    }

    /**
     * 查找指定范围内的可用端口,最大值为65535
     * 此方法只检测给定范围内的随机一个端口,检测65535-minPort次
     * 来自org.springframework.util.SocketUtils
     *
     * @param minPort 端口最小值（包含）
     * @return 可用的端口
     */
    public static int getUsableLocalPort(int minPort) {
        return getUsableLocalPort(minPort, PORT_RANGE_MAX);
    }

    /**
     * 查找指定范围内的可用端口
     * 此方法只检测给定范围内的随机一个端口,检测maxPort-minPort次
     * 来自org.springframework.util.SocketUtils
     *
     * @param minPort 端口最小值（包含）
     * @param maxPort 端口最大值（包含）
     * @return 可用的端口
     */
    public static int getUsableLocalPort(int minPort, int maxPort) {
        for (int i = minPort; i <= maxPort; i++) {
            if (isUsableLocalPort(RandomUtils.randomInt(minPort, maxPort + 1))) {
                return i;
            }
        }

        throw new InstrumentException("Could not find an available port in the range [{" + minPort + "}, {" + maxPort + "}] after {" + (maxPort - minPort) + "} attempts");
    }

    /**
     * 获取多个本地可用端口
     * 来自org.springframework.util.SocketUtils
     *
     * @param numRequested int
     * @param minPort      端口最小值（包含）
     * @param maxPort      端口最大值（包含）
     * @return 可用的端口
     */
    public static TreeSet<Integer> getUsableLocalPorts(int numRequested, int minPort, int maxPort) {
        final TreeSet<Integer> availablePorts = new TreeSet<>();
        int attemptCount = 0;
        while ((++attemptCount <= numRequested + 100) && availablePorts.size() < numRequested) {
            availablePorts.add(getUsableLocalPort(minPort, maxPort));
        }

        if (availablePorts.size() != numRequested) {
            throw new InstrumentException("Could not find {" + numRequested + "} available  ports in the range [{" + minPort + "}, {" + maxPort + "}]");
        }

        return availablePorts;
    }

    /**
     * 判定是否为内网IP
     * 私有IP：A类 10.0.0.0-10.255.255.255 B类 172.16.0.0-172.31.255.255 C类 192.168.0.0-192.168.255.255 当然,还有127这个网段是环回地址
     *
     * @param ipAddress IP地址
     * @return 是否为内网IP
     */
    public static boolean isInnerIP(String ipAddress) {
        boolean isInnerIp;
        long ipNum = NetUtils.ipv4ToLong(ipAddress);

        long aBegin = NetUtils.ipv4ToLong("10.0.0.0");
        long aEnd = NetUtils.ipv4ToLong("10.255.255.255");

        long bBegin = NetUtils.ipv4ToLong("172.16.0.0");
        long bEnd = NetUtils.ipv4ToLong("172.31.255.255");

        long cBegin = NetUtils.ipv4ToLong("192.168.0.0");
        long cEnd = NetUtils.ipv4ToLong("192.168.255.255");

        isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || ipAddress.equals(LOCAL_IP);
        return isInnerIp;
    }

    /**
     * 相对URL转换为绝对URL
     *
     * @param absoluteBasePath 基准路径,绝对
     * @param relativePath     相对路径
     * @return 绝对URL
     */
    public static String toAbsoluteUrl(String absoluteBasePath, String relativePath) {
        try {
            URL absoluteUrl = new URL(absoluteBasePath);
            return new URL(absoluteUrl, relativePath).toString();
        } catch (Exception e) {
            throw new InstrumentException("To absolute url [{" + relativePath + "}] base [{" + absoluteBasePath + "}] error!");
        }
    }

    /**
     * 隐藏掉IP地址的最后一部分为 * 代替
     *
     * @param ip IP地址
     * @return 隐藏部分后的IP
     */
    public static String hideIpPart(String ip) {
        return new StringBuffer(ip.length()).append(ip, 0, ip.lastIndexOf(Symbol.DOT) + 1).append(Symbol.STAR).toString();
    }

    /**
     * 隐藏掉IP地址的最后一部分为 * 代替
     *
     * @param ip IP地址
     * @return 隐藏部分后的IP
     */
    public static String hideIpPart(long ip) {
        return hideIpPart(longToIpv4(ip));
    }

    /**
     * 构建InetSocketAddress
     * 当host中包含端口时（用“：”隔开）,使用host中的端口,否则使用默认端口
     * 给定host为空时使用本地host（127.0.0.1）
     *
     * @param host        Host
     * @param defaultPort 默认端口
     * @return InetSocketAddress
     */
    public static InetSocketAddress buildInetSocketAddress(String host, int defaultPort) {
        if (StringUtils.isBlank(host)) {
            host = LOCAL_IP;
        }

        String destHost;
        int port;
        int index = host.indexOf(Symbol.COLON);
        if (index != -1) {
            // host:port形式
            destHost = host.substring(0, index);
            port = Integer.parseInt(host.substring(index + 1));
        } else {
            destHost = host;
            port = defaultPort;
        }

        return new InetSocketAddress(destHost, port);
    }

    /**
     * 通过域名得到IP
     *
     * @param hostName HOST
     * @return ip address or hostName if UnknownHostException
     */
    public static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }

    /**
     * 获取本机所有网卡
     *
     * @return 所有网卡, 异常返回<code>null</code>
     * @since 3.0.1
     */
    public static Collection<NetworkInterface> getNetworkInterfaces() {
        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return null;
        }

        return CollUtils.addAll(new ArrayList<>(), networkInterfaces);
    }

    /**
     * 获得本机的IPv4地址列表
     * 返回的IP列表有序,按照系统设备顺序
     *
     * @return IP地址列表 {@link LinkedHashSet}
     */
    public static LinkedHashSet<String> localIpv4s() {
        final LinkedHashSet<InetAddress> localAddressList = localAddressList(t -> t instanceof Inet4Address);

        return toIpList(localAddressList);
    }

    /**
     * 获得本机的IPv6地址列表
     * 返回的IP列表有序,按照系统设备顺序
     *
     * @return IP地址列表 {@link LinkedHashSet}
     */
    public static LinkedHashSet<String> localIpv6s() {
        final LinkedHashSet<InetAddress> localAddressList = localAddressList(t -> t instanceof Inet6Address);

        return toIpList(localAddressList);
    }

    /**
     * 地址列表转换为IP地址列表
     *
     * @param addressList 地址{@link Inet4Address} 列表
     * @return IP地址字符串列表
     */
    public static LinkedHashSet<String> toIpList(Set<InetAddress> addressList) {
        final LinkedHashSet<String> ipSet = new LinkedHashSet<>();
        for (InetAddress address : addressList) {
            ipSet.add(address.getHostAddress());
        }

        return ipSet;
    }

    /**
     * 获得本机的IP地址列表（包括Ipv4和Ipv6）
     * 返回的IP列表有序,按照系统设备顺序
     *
     * @return IP地址列表 {@link LinkedHashSet}
     */
    public static LinkedHashSet<String> localIps() {
        final LinkedHashSet<InetAddress> localAddressList = localAddressList(null);
        return toIpList(localAddressList);
    }

    /**
     * 获取所有满足过滤条件的本地IP地址对象
     *
     * @param addressFilter 过滤器,null表示不过滤,获取所有地址
     * @return 过滤后的地址对象列表
     */
    public static LinkedHashSet<InetAddress> localAddressList(Filter<InetAddress> addressFilter) {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new InstrumentException(e);
        }

        if (networkInterfaces == null) {
            throw new InstrumentException("Get network interface error!");
        }

        final LinkedHashSet<InetAddress> ipSet = new LinkedHashSet<>();

        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = networkInterfaces.nextElement();
            final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                final InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress != null && (null == addressFilter || addressFilter.accept(inetAddress))) {
                    ipSet.add(inetAddress);
                }
            }
        }

        return ipSet;
    }

    /**
     * 获取本机网卡IP地址,这个地址为所有网卡中非回路地址的第一个
     * 如果获取失败调用 {@link InetAddress#getLocalHost()}方法获取
     * 此方法不会抛出异常,获取失败将返回<code>null</code>
     * <p>
     * 参考：http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
     *
     * @return 本机网卡IP地址, 获取失败返回<code>null</code>
     * @since 3.0.7
     */
    public static String getLocalhostStr() {
        InetAddress localhost = getLocalhost();
        if (null != localhost) {
            return localhost.getHostAddress();
        }
        return null;
    }

    /**
     * 获取本机网卡IP地址,规则如下：
     *
     * <pre>
     * 1. 查找所有网卡地址,必须非回路（loopback）地址、非局域网地址（siteLocal）、IPv4地址
     * 2. 如果无满足要求的地址,调用 {@link InetAddress#getLocalHost()} 获取地址
     * </pre>
     * <p>
     * 此方法不会抛出异常,获取失败将返回<code>null</code>
     *
     * @return 本机网卡IP地址, 获取失败返回<code>null</code>
     * @since 3.0.1
     */
    public static InetAddress getLocalhost() {
        final LinkedHashSet<InetAddress> localAddressList = localAddressList(address -> {
            // 非loopback地址,指127.*.*.*的地址
            return false == address.isLoopbackAddress()
                    // 非地区本地地址,指10.0.0.0 ~ 10.255.255.255、172.16.0.0 ~ 172.31.255.255、192.168.0.0 ~ 192.168.255.255
                    && false == address.isSiteLocalAddress()
                    // 需为IPV4地址
                    && address instanceof Inet4Address;
        });

        if (CollUtils.isNotEmpty(localAddressList)) {
            return CollUtils.get(localAddressList, 0);
        }

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            // ignore
        }

        return null;
    }

    /**
     * 获得本机MAC地址
     *
     * @return 本机MAC地址
     */
    public static String getLocalMacAddress() {
        return getMacAddress(getLocalhost());
    }

    /**
     * 获得指定地址信息中的MAC地址,使用分隔符“-”
     *
     * @param inetAddress {@link InetAddress}
     * @return MAC地址, 用-分隔
     */
    public static String getMacAddress(InetAddress inetAddress) {
        return getMacAddress(inetAddress, Symbol.HYPHEN);
    }

    /**
     * 获得指定地址信息中的MAC地址
     *
     * @param inetAddress {@link InetAddress}
     * @param separator   分隔符,推荐使用“-”或者“:”
     * @return MAC地址, 用-分隔
     */
    public static String getMacAddress(InetAddress inetAddress, String separator) {
        if (null == inetAddress) {
            return null;
        }

        byte[] mac;
        try {
            mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
        } catch (SocketException e) {
            throw new InstrumentException(e);
        }
        if (null != mac) {
            final StringBuilder sb = new StringBuilder();
            String s;
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append(separator);
                }
                // 字节转换为整数
                s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * 创建 {@link InetSocketAddress}
     *
     * @param host 域名或IP地址,空表示任意地址
     * @param port 端口,0表示系统分配临时端口
     * @return {@link InetSocketAddress}
     * @since 3.3.0
     */
    public static InetSocketAddress createAddress(String host, int port) {
        if (StringUtils.isBlank(host)) {
            return new InetSocketAddress(port);
        }
        return new InetSocketAddress(host, port);
    }

    /**
     * 简易的使用Socket发送数据
     *
     * @param host    Server主机
     * @param port    Server端口
     * @param isBlock 是否阻塞方式
     * @param data    需要发送的数据
     * @throws InstrumentException IO异常
     * @since 3.3.0
     */
    public static void netCat(String host, int port, boolean isBlock, ByteBuffer data) throws InstrumentException {
        try (SocketChannel channel = SocketChannel.open(createAddress(host, port))) {
            channel.configureBlocking(isBlock);
            channel.write(data);
        } catch (IOException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 使用普通Socket发送数据
     *
     * @param host Server主机
     * @param port Server端口
     * @param data 数据
     * @throws InstrumentException 异常
     * @since 3.3.0
     */
    public static void netCat(String host, int port, byte[] data) throws InstrumentException {
        OutputStream out = null;
        try (Socket socket = new Socket(host, port)) {
            out = socket.getOutputStream();
            out.write(data);
            out.flush();
        } catch (IOException e) {
            throw new InstrumentException(e);
        } finally {
            IoUtils.close(out);
        }
    }

    /**
     * 是否在CIDR规则配置范围内
     * 方法来自：【成都】小邓
     *
     * @param ip   需要验证的IP
     * @param cidr CIDR规则
     * @return 是否在范围内
     */
    public static boolean isInRange(String ip, String cidr) {
        String[] ips = StringUtils.splitToArray(ip, Symbol.C_DOT);
        int ipAddr = (Integer.parseInt(ips[0]) << 24) | (Integer.parseInt(ips[1]) << 16) | (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
        int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
        int mask = 0xFFFFFFFF << (32 - type);
        String cidrIp = cidr.replaceAll("/.*", "");
        String[] cidrIps = cidrIp.split("\\.");
        int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24) | (Integer.parseInt(cidrIps[1]) << 16) | (Integer.parseInt(cidrIps[2]) << 8) | Integer.parseInt(cidrIps[3]);
        return (ipAddr & mask) == (cidrIpAddr & mask);
    }

    /**
     * Unicode域名转puny code
     *
     * @param unicode Unicode域名
     * @return puny code
     */
    public static String idnToASCII(String unicode) {
        return IDN.toASCII(unicode);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(Symbol.COMMA) > 0) {
            final String[] ips = ip.trim().split(Symbol.COMMA);
            for (String subIp : ips) {
                if (false == isUnknow(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 检测给定字符串是否为未知,多用于检测HTTP请求相关
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    public static boolean isUnknow(String checkString) {
        return StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 根据IP地址，及IP白名单设置规则判断IP是否包含在白名单
     * 使用场景： 是否包含在白名单或黑名单中.
     *
     * @param ip           ip address
     * @param ipRuleConfig ip rule config
     * @return contain return true
     */
    public static boolean containIp(String ip, String ipRuleConfig) {
        return checkContainIp(ip, getAllowIpList(ipRuleConfig));
    }

    /**
     * 根据IP白名单设置获取可用的IP列表
     *
     * @param allowIp allow ip address
     * @return allow ip address list
     */
    private static Set<String> getAllowIpList(String allowIp) {
        Set<String> ipList = new HashSet<>();
        for (String allow : allowIp.replaceAll("\\s", "").split(";")) {
            if (allow.contains("*")) {
                String[] ips = allow.split("\\.");
                String[] from = new String[]{"0", "0", "0", "0"};
                String[] end = new String[]{"255", "255", "255", "255"};
                List<String> tem = new ArrayList<>();
                for (int i = 0; i < ips.length; i++) {
                    if (ips[i].contains("*")) {
                        tem = complete(ips[i]);
                        from[i] = null;
                        end[i] = null;
                    } else {
                        from[i] = ips[i];
                        end[i] = ips[i];
                    }
                }
                StringBuilder fromIP = new StringBuilder();
                StringBuilder endIP = new StringBuilder();
                for (int i = 0; i < 4; i++) {
                    if (from[i] != null) {
                        fromIP.append(from[i]).append(".");
                        endIP.append(end[i]).append(".");
                    } else {
                        fromIP.append("[*].");
                        endIP.append("[*].");
                    }
                }
                fromIP.deleteCharAt(fromIP.length() - 1);
                endIP.deleteCharAt(endIP.length() - 1);

                for (String s : tem) {
                    String ip = fromIP.toString().replace("[*]",
                            s.split(";")[0]) + Symbol.HYPHEN + endIP.toString().replace("[*]",
                            s.split(";")[1]);
                    if (validate(ip)) {
                        ipList.add(ip);
                    }
                }
            } else {
                if (validate(allow)) {
                    ipList.add(allow);
                }
            }

        }

        return ipList;
    }

    /**
     * 根据IP,及可用Ip列表来判断ip是否包含在白名单之中
     *
     * @param ip     ip address
     * @param ipList ip address list
     * @return contain return true
     */
    private static boolean checkContainIp(String ip, Set<String> ipList) {
        if (ipList.isEmpty() || ipList.contains(ip)) {
            return true;
        } else {
            for (String allow : ipList) {
                if (allow.contains(Symbol.HYPHEN)) {
                    String[] from = allow.split(Symbol.HYPHEN)[0].split("\\.");
                    String[] end = allow.split(Symbol.HYPHEN)[1].split("\\.");
                    String[] tag = ip.split("\\.");

                    // 对IP从左到右进行逐段匹配
                    boolean check = true;
                    for (int i = 0; i < 4; i++) {
                        int s = Integer.valueOf(from[i]);
                        int t = Integer.valueOf(tag[i]);
                        int e = Integer.valueOf(end[i]);
                        if (!(s <= t && t <= e)) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 对单个IP节点进行范围限定
     *
     * @param arg ip address cell
     * @return 返回限定后的IP范围，格式为List[10;19, 100;199]
     */
    private static List<String> complete(String arg) {
        List<String> com = new ArrayList<>();
        if (arg.length() == 1) {
            com.add("0;255");
        } else if (arg.length() == 2) {
            String s1 = complete(arg, 1);
            if (s1 != null) {
                com.add(s1);
            }

            String s2 = complete(arg, 2);
            if (s2 != null) {
                com.add(s2);
            }
        } else {
            String s1 = complete(arg, 1);
            if (s1 != null) {
                com.add(s1);
            }
        }

        return com;
    }

    private static String complete(String arg, int length) {
        String from, end;
        if (length == 1) {
            from = arg.replace("*", "0");
            end = arg.replace("*", "9");
        } else {
            from = arg.replace("*", "00");
            end = arg.replace("*", "99");
        }

        if (Integer.valueOf(from) > 255) {
            return null;
        }
        if (Integer.valueOf(end) > 255) {
            end = "255";
        }

        return from + ";" + end;
    }

    /**
     * 在添加至白名单时进行格式校验
     *
     * @param ip ip address
     * @return success true
     */
    private static boolean validate(String ip) {
        for (String s : ip.split(Symbol.HYPHEN)) {
            if (!RegEx.IPV4.matcher(s).matches()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 指定IP的long是否在指定范围内
     *
     * @param userIp 用户IP
     * @param begin  开始IP
     * @param end    结束IP
     * @return 是否在范围内
     */
    private static boolean isInner(long userIp, long begin, long end) {
        return (userIp >= begin) && (userIp <= end);
    }

    /**
     * 检测IP地址是否能ping通
     *
     * @param ip IP地址
     * @return the true/false 返回是否ping通
     */
    public static boolean ping(String ip) {
        return ping(ip, 200);
    }

    /**
     * 检测IP地址是否能ping通
     *
     * @param ip      IP地址
     * @param timeout 检测超时（毫秒）
     * @return the true/false 是否ping通
     */
    public static boolean ping(String ip, int timeout) {
        try {
            return InetAddress.getByName(ip).isReachable(timeout);
        } catch (Exception ex) {
            return false;
        }
    }

}
