package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class BaseUtil {
    public static final Properties STATIC_PROPERTIES = new Properties();

    static {
        try {
            InputStream in = BaseUtil.class.getClassLoader().getResourceAsStream("env.properties");
            if (null != in) {
                STATIC_PROPERTIES.load(new InputStreamReader(in,"UTF-8"));
            }
            System.out.println("=========加载propertity文件======================================");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    /**
     * 获取系统当前时间
     *
     * @return
     */
    public String getDate() {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = dateformat.format(System.currentTimeMillis());
        return dateStr;
    }
}
