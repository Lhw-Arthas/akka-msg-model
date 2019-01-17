package lhw.demo.akka.msg;

import com.amazonaws.util.EC2MetadataUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by lhwarthas on 19/1/17.
 */

public class IPUtils {
    public static List<String> getLocalIps() {
        List<String> ips = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface iface = enumeration.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp()) continue;

                Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    String ip = inetAddresses.nextElement().getHostAddress();
                    // 排除 回环IP/ipv6 地址
                    if (ip.contains(":")) continue;
                    if (StringUtils.isNotBlank(ip)) ips.add(ip);
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return ips;
    }

    public static String getLocalIntranetIp() {
        List<String> ips = getLocalIps();
        for (String ip : ips) {
            if (isIntranetIp(ip)) return ip;
        }
        return "";
    }

    public static boolean isIntranetIp(String ip) {
        try {
            if (ip.startsWith("10.") || ip.startsWith("192.168.")) return true;
            // 172.16.x.x～172.31.x.x
            String[] ns = ip.split("\\.");
            int ipSub = Integer.valueOf(ns[0] + ns[1]);
            if (ipSub >= 17216 && ipSub <= 17231) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getAwsIpAddress() {
        String ipAddress = EC2MetadataUtils.getPrivateIpAddress();
        if (null == ipAddress || "".equals(ipAddress)) {
            ipAddress = IPUtils.getLocalIntranetIp();
        }
        return ipAddress;
    }
}
