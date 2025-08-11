package org.example.multidbapp.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.multidbapp.model.response.dataSource.DataSourceFullResponse;
import org.example.multidbapp.model.response.dataSource.DataSourceProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@ConfigurationProperties(prefix = "db")
public class DataSourceCache {
    @Getter
    private List<DataSourceProperty> dataSources = new ArrayList<>();


    @JsonIgnore
    private List<DataSourceFullResponse> dataSourceFullResponses = new ArrayList<>();

    @JsonIgnore
    private static final Map<String, String> driverClassName = Map.of(
            "mysql", "com.mysql.cj.jdbc.Driver",
            "postgres", "org.postgresql.Driver",
            "oracle", "oracle.jdbc.driver.OracleDriver",
            "sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver",
            "h2", "org.h2.Driver",
            "sqlite", "org.sqlite.JDBC"
    );


    @PostConstruct
    public void init() {
        dataSourceFullResponses = getDataSourceList();
    }

    public List<DataSourceFullResponse> dataSourceFullResponses() {
        return new ArrayList<>(dataSourceFullResponses);
    }

    public List<DataSourceFullResponse> getDataSourceList() {
        List<DataSourceFullResponse> dataSourceList = new ArrayList<>();
        for (DataSourceProperty dataSourceDto : dataSources) {
            HikariDataSource hikariDataSource = new HikariDataSource();
            hikariDataSource.setJdbcUrl(dataSourceDto.url());
            hikariDataSource.setUsername(dataSourceDto.user());
            hikariDataSource.setPassword(dataSourceDto.password());
            hikariDataSource.setDriverClassName(driverClassName.get(dataSourceDto.strategy()));
            hikariDataSource.setMaximumPoolSize(5);
            dataSourceList.add(new DataSourceFullResponse(hikariDataSource, dataSourceDto));
        }
        return dataSourceList;
    }

    @PreDestroy
    public void closeAll() {
        dataSourceFullResponses.forEach(dataSourceFullResponse -> {
            if (dataSourceFullResponse.dataSource() instanceof HikariDataSource hikariDataSource){
                hikariDataSource.close();
            }
        });
    }

}
