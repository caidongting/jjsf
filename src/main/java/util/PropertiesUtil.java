package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by caidt on 2016/12/2.
 */
public abstract class PropertiesUtil {

    public static Properties load(String absolutePath, String fileName) {
        // 尝试从绝对路径加载
        Properties props = new Properties();
        final String fileAbsPath = absolutePath + fileName;
        try {
            FileInputStream is = new FileInputStream(new File(fileAbsPath));
            props.load(is);
        } catch (Exception e1) {
            // 尝试从classpath下加载
            try {
                final InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
                props.load(inputStream);
            } catch (IOException e) {
                throw new RuntimeException("加载失败:", e);
            }
        }
        return props;
    }
}
