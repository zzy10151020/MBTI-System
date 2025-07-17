package org.frostedstar.mbtisystem.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具类
 */
@Slf4j
public class DatabaseUtil {
    
    private static HikariDataSource dataSource;
    private static boolean initialized = false;
    
    /**
     * 初始化数据源
     */
    private static synchronized void init() {
        if (initialized) {
            return;
        }
        
        try {
            Properties props = new Properties();
            InputStream inputStream = DatabaseUtil.class.getClassLoader()
                    .getResourceAsStream("application.properties");
            
            if (inputStream != null) {
                props.load(inputStream);
                inputStream.close();
            }
            
            // 显式加载MySQL驱动
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                log.error("MySQL驱动加载失败", e);
                throw new RuntimeException("MySQL驱动加载失败", e);
            }
            
            HikariConfig config = new HikariConfig();
            // 不再通过HikariConfig设置驱动类名，而是直接设置JDBC URL
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            
            // 连接池配置
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxActive", "20")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "2")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.maxWait", "60000")));
            config.setValidationTimeout(5000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setLeakDetectionThreshold(60000);
            
            dataSource = new HikariDataSource(config);
            initialized = true;
            log.info("数据库连接池初始化成功");
            
        } catch (IOException e) {
            log.error("读取数据库配置失败", e);
            throw new RuntimeException("数据库连接池初始化失败", e);
        }
    }
    
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            init();
        }
        return dataSource.getConnection();
    }
    
    /**
     * 获取数据源
     */
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * 关闭数据源
     */
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            log.info("数据库连接池已关闭");
        }
    }
    
    /**
     * 关闭数据库连接资源
     */
    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    log.warn("关闭资源时发生异常", e);
                }
            }
        }
    }
}
