package org.example.multidbapp.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.multidbapp.model.response.dataSource.DataSourceMappingProperty;
import org.example.multidbapp.model.response.dataSource.DataSourceProperty;
import org.example.multidbapp.model.GlobalResponse;
import org.example.multidbapp.model.response.user.UserResponse;
import org.example.multidbapp.service.DataSourceCache;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.multidbapp.utils.QueryUtils.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final DataSourceCache dataSourceCache;

    @Override
    public GlobalResponse<List<UserResponse>> getAllUsers() {
        log.info("Try to get all users");
        List<UserResponse> userResponses = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(dataSourceCache.dataSourceFullResponses().size());

        List<CompletableFuture<Void>> futures = dataSourceCache.dataSourceFullResponses().stream()
                .map(dataSourceFullResponse -> CompletableFuture.runAsync(() -> {
                    DataSource dataSource = dataSourceFullResponse.dataSource();
                    DataSourceProperty dataSourceProperty = dataSourceFullResponse.dataSourceProperty();
                    DataSourceMappingProperty dataSourcePropertyMapping = dataSourceProperty.mapping();
                    String query = String.format(
                            GET_ALL_QUERY,
                            getSelectFields(dataSourcePropertyMapping, dataSourceProperty.strategy()),
                            addQuoteToName(dataSourceProperty.table(), dataSourceProperty.strategy()),
                            addQuoteToName(dataSourcePropertyMapping.id(), dataSourceProperty.strategy())
                    );
                    addUsersFromDb(userResponses, dataSource, dataSourcePropertyMapping, query);
                }, executor))
                .toList();

        futures.forEach(CompletableFuture::join);

        executor.shutdown();
        log.info("Success in get all users");
        return new GlobalResponse<>(true, userResponses);
    }

    @Override
    public GlobalResponse<List<UserResponse>> getAllUsersWithFilter(String filter) {
        log.info("Try to get all users with filter:{}", filter);
        List<UserResponse> userResponses = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(dataSourceCache.dataSourceFullResponses().size());

        List<CompletableFuture<Void>> futures = dataSourceCache.dataSourceFullResponses().stream().map(dataSourceFullResponse -> CompletableFuture.runAsync(() -> {
            DataSource dataSource = dataSourceFullResponse.dataSource();
            DataSourceProperty dataSourceProperty = dataSourceFullResponse.dataSourceProperty();
            DataSourceMappingProperty dataSourcePropertyMapping = dataSourceProperty.mapping();
            String filterValue = "%" + filter.toLowerCase().trim() + "%";
            String query = String.format(
                    GET_ALL_QUERY_WITH_FILTER,
                    getSelectFields(dataSourcePropertyMapping, dataSourceProperty.strategy()),
                    addQuoteToName(dataSourceProperty.table(), dataSourceProperty.strategy()),
                    addQuoteToName(dataSourcePropertyMapping.username(), dataSourceProperty.strategy()),
                    filterValue,
                    addQuoteToName(dataSourcePropertyMapping.name(), dataSourceProperty.strategy()),
                    filterValue,
                    addQuoteToName(dataSourcePropertyMapping.surname(), dataSourceProperty.strategy()),
                    filterValue
            );
            addUsersFromDb(userResponses, dataSource, dataSourcePropertyMapping, query);
        })).toList();

        futures.forEach(CompletableFuture::join);
        executor.shutdown();

        log.info("Success in get all users with filter");
        return new GlobalResponse<>(true, userResponses);
    }

    private void addUsersFromDb(List<UserResponse> userResponses, DataSource dataSource, DataSourceMappingProperty dataSourcePropertyMapping, String query){
        log.info("Sql query: {}", query);
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ){
            while (rs.next()){
                userResponses.add(new UserResponse(
                        rs.getLong(dataSourcePropertyMapping.id()),
                        rs.getString(dataSourcePropertyMapping.username()),
                        rs.getString(dataSourcePropertyMapping.name()),
                        rs.getString(dataSourcePropertyMapping.surname())
                ));
            }
        }catch (SQLException e){
            log.error("Error in get users", e);
        }
    }
}
