package com.bigo.common.utils.ip;

import com.bigo.common.utils.StringUtils;
import com.bigo.common.utils.file.FileUtils;
import com.bigo.common.utils.html.EscapeUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取IP方法
 *
 * @author bigo
 */
@Slf4j
public class IpUtils {
    /*private static final byte[] dbPath;
    static {
        *//*dbPath = IpUtils.class.getResource("/ip2region.db").getPath();
        File file = new File(dbPath);
        if (!file.exists()) {
            System.out.println("Error: Invalid ip2region.db file");
        }*//*
        try {
            ClassPathResource resource = new ClassPathResource("/ip2region.db");
            InputStream inputStream = resource.getInputStream();
            byte[] dbBinStr = FileUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public static String getIpAddr(HttpServletRequest request)
    {
        if (request == null)
        {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }
        ip = EscapeUtil.clean(ip);// 清除Xss特殊字符
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    public static boolean internalIp(String ip)
    {
        byte[] addr = textToNumericFormatV4(ip);
        return internalIp(addr) || "127.0.0.1".equals(ip);
    }

    private static boolean internalIp(byte[] addr)
    {
        if (StringUtils.isNull(addr) || addr.length < 2)
        {
            return true;
        }
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        // 172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        // 192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0)
        {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4)
                {
                    return true;
                }
            case SECTION_5:
                switch (b1)
                {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }

    /**
     * 将IPv4地址转换成字节
     *
     * @param text IPv4地址
     * @return byte 字节
     */
    public static byte[] textToNumericFormatV4(String text)
    {
        if (text.length() == 0)
        {
            return null;
        }

        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);
        try
        {
            long l;
            int i;
            switch (elements.length)
            {
                case 1:
                    l = Long.parseLong(elements[0]);
                    if ((l < 0L) || (l > 4294967295L))
                        return null;
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    if ((l < 0L) || (l > 255L))
                        return null;
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if ((l < 0L) || (l > 16777215L))
                        return null;
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < 2; ++i)
                    {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L))
                            return null;
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    if ((l < 0L) || (l > 65535L))
                        return null;
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < 4; ++i)
                    {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L))
                            return null;
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        return bytes;
    }

    public static String getHostIp()
    {
        try
        {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
        }
        return "127.0.0.1";
    }

    public static String getHostName()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
        }
        return "未知";
    }

    private static final String UNKOWN_ADDRESS = "未知位置";

    /**
     * 根据IP获取地址
     *
     * @return 国家|区域|省份|城市|ISP
     */
    public static String getAddress(String ip) {
        return getAddress(ip, DbSearcher.BTREE_ALGORITHM);
    }

    /**
     * 根据IP获取地址位置
     *
     * @return 国家_区域_省份_城市
     */
    public static String getPosition(String address) {
        String [] positions = address.split("\\|");
        StringBuilder builder = new StringBuilder();
        for (String position : positions) {
            if(position.equals("0") || position.contains("电信")
                    || position.contains("联通") || position.contains("移动")) {
                continue;
            }
            builder.append(position).append("_");
        }
        return builder.substring(0, builder.length()-1).toString();
    }

    /**
     * 根据IP获取地址
     *
     * @param ip
     * @param algorithm 查询算法
     * @return 国家|区域|省份|城市|ISP
     * @see DbSearcher
     * DbSearcher.BTREE_ALGORITHM; //B-tree
     * DbSearcher.BINARY_ALGORITHM //Binary
     * DbSearcher.MEMORY_ALGORITYM //Memory
     */
    @SneakyThrows
    public static String getAddress(String ip, int algorithm) {
        if (!Util.isIpAddress(ip)) {
            log.error("错误格式的ip地址: {}", ip);
            return UNKOWN_ADDRESS;
        }
        /*File file = new File(dbPath);
        if (!file.exists()) {
            log.error("地址库文件不存在");
            return UNKOWN_ADDRESS;
        }*/
        ClassPathResource resource = new ClassPathResource("/ip2region.db");
        InputStream inputStream = resource.getInputStream();
        byte[] dbBinStr = FileUtils.toByteArray(inputStream);
        DbSearcher searcher = new DbSearcher(new DbConfig(), dbBinStr);
       /* DataBlock dataBlock;
        switch (algorithm) {
            case DbSearcher.BTREE_ALGORITHM:
                dataBlock = searcher.btreeSearch(ip);
                break;
            case DbSearcher.BINARY_ALGORITHM:
                dataBlock = searcher.binarySearch(ip);
                break;
            case DbSearcher.MEMORY_ALGORITYM:
                dataBlock = searcher.memorySearch(ip);
                break;
            default:
                log.error("未传入正确的查询算法");
                return UNKOWN_ADDRESS;
        }*/
        Method method = searcher.getClass().getMethod("memorySearch", String.class);
        String addr = ((DataBlock) method.invoke(searcher, ip)).getRegion();
        return addr;
    }

    public static void main(String[] args) {
        System.out.println(getAddress("163.181.38.181"));
    }
}