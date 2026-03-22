package edu.bbte.idde.ogim2385.data.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class ConnectionManager {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/webshop");
        config.setUsername("root");
        config.setPassword("root");
        config.setMaximumPoolSize(10);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
