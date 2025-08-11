package org.example.multidbapp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.multidbapp.model.GlobalResponse;
import org.example.multidbapp.model.response.dataSource.DataSourceFullResponse;
import org.example.multidbapp.model.response.dataSource.DataSourceMappingProperty;
import org.example.multidbapp.model.response.dataSource.DataSourceProperty;
import org.example.multidbapp.model.response.user.UserResponse;
import org.example.multidbapp.service.DataSourceCache;
import org.example.multidbapp.service.user.UserServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IntegrationTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("multidbapp")
            .withUsername("userpg")
            .withPassword("userpg");

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("multidbapp")
            .withUsername("userms")
            .withPassword("userms");

    private static DataSource createDataSource(JdbcDatabaseContainer<?> container) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(container.getJdbcUrl());
        config.setUsername(container.getUsername());
        config.setPassword(container.getPassword());
        return new HikariDataSource(config);
    }

    static UserServiceImpl userService;
    static DataSourceCache dataSourceCacheMock;

    @BeforeAll
    static void setup() throws Exception {
        postgres.start();
        mysql.start();

        try (Connection conn = postgres.createConnection("")) {
            try (Statement st = conn.createStatement()) {
                st.execute("CREATE TABLE users (id BIGINT PRIMARY KEY, username_test VARCHAR(50), first_name VARCHAR(50), last_name VARCHAR(50))");
                st.execute("INSERT INTO users (id, username_test, first_name, last_name) VALUES (1, 'pguser1', 'Postgres', 'User1')");
            }
        }

        try (Connection conn = mysql.createConnection("")) {
            try (Statement st = conn.createStatement()) {
                st.execute("CREATE TABLE users_mysql (idusers_mysql BIGINT PRIMARY KEY, users_username VARCHAR(50), users_name VARCHAR(50), users_surname VARCHAR(50))");
                st.execute("INSERT INTO users_mysql (idusers_mysql, users_username, users_name, users_surname) VALUES (2, 'mysqluser1', 'MySQL', 'User1')");
            }
        }

        dataSourceCacheMock = new DataSourceCache() {
            @Override
            public List<DataSourceFullResponse> dataSourceFullResponses() {
                return List.of(
                        new DataSourceFullResponse(createDataSource(postgres),
                                new DataSourceProperty("db-1", "postgres", "jdbc:postgresql://localhost:5432/multidbapp", "users", "userpg", "userpg",
                                        new DataSourceMappingProperty("id", "username_test", "first_name", "last_name"))),
                        new DataSourceFullResponse(createDataSource(mysql),
                                new DataSourceProperty("db-2", "mysql", "jdbc:mysql://localhost:3306/multidbapp", "users_mysql", "userms", "userms",
                                        new DataSourceMappingProperty("idusers_mysql", "users_username", "users_name", "users_surname")))
                );
            }
        };

        userService = new UserServiceImpl(dataSourceCacheMock);
    }

    @AfterAll
    static void cleanup() {
        postgres.stop();
        mysql.stop();
    }

    @Test
    void testGetAllUsers() {
        GlobalResponse<List<UserResponse>> response = userService.getAllUsers();

        assertTrue(response.isSuccess());
        Assertions.assertNotNull(response.data());
        assertEquals(2, response.data().size());

        assertTrue(response.data().stream().anyMatch(u -> u.username().equals("pguser1")));
        assertTrue(response.data().stream().anyMatch(u -> u.username().equals("mysqluser1")));
    }

    @Test
    void testGetAllUsersWithFilter() {
        GlobalResponse<List<UserResponse>> response = userService.getAllUsersWithFilter("pg");

        assertTrue(response.isSuccess());
        Assertions.assertNotNull(response.data());
        assertEquals(1, response.data().size());

        assertTrue(response.data().stream().anyMatch(u -> u.username().equals("pguser1")));
    }
}
