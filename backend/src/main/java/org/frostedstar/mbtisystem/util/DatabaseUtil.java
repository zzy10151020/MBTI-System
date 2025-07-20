package org.frostedstar.mbtisystem.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
            
            // 添加连接池健壮性配置
            config.setConnectionTestQuery("SELECT 1");
            
            // MySQL 特定配置，提高连接稳定性
            config.addDataSourceProperty("autoReconnect", "true");
            config.addDataSourceProperty("failOverReadOnly", "false");
            config.addDataSourceProperty("maxReconnects", "3");
            config.addDataSourceProperty("initialTimeout", "2");
            config.addDataSourceProperty("useUnicode", "true");
            config.addDataSourceProperty("characterEncoding", "utf8");
            config.addDataSourceProperty("serverTimezone", "Asia/Shanghai");
            config.addDataSourceProperty("useSSL", "false");
            config.addDataSourceProperty("allowPublicKeyRetrieval", "true");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
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
     * 检查数据库连接健康状态
     */
    public static boolean isConnectionHealthy() {
        if (!initialized || dataSource == null) {
            return false;
        }
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1")) {
            
            stmt.executeQuery();
            return true;
            
        } catch (SQLException e) {
            log.warn("数据库连接健康检查失败", e);
            return false;
        }
    }
    
    /**
     * 获取连接池状态信息
     */
    public static String getPoolStatus() {
        if (!initialized || dataSource == null) {
            return "连接池未初始化";
        }
        
        try {
            return String.format("连接池状态 - 活跃连接: %d, 空闲连接: %d, 等待获取连接的线程: %d", 
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        } catch (Exception e) {
            return "无法获取连接池状态: " + e.getMessage();
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
