package edu.bbte.idde.data.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.bbte.idde.config.ApplicationConfig;
import edu.bbte.idde.config.ConfigLoader;

import javax.sql.DataSource;

public class ConnectionManager {
    private static HikariDataSource dataSource;

    static {
        ApplicationConfig.JdbcConfig cfg = ConfigLoader.load().getJdbc();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(cfg.getUrl());
        config.setUsername(cfg.getUsername());
        config.setPassword(cfg.getPassword());
        config.setMaximumPoolSize(10);
        config.setDriverClassName(cfg.getDriver());
        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
