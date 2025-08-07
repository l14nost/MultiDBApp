package org.example.multidbapp.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.multidbapp.model.dataSource.DataSourceMappingProperty;
import org.example.multidbapp.model.dataSource.DataSourceProperty;
import org.example.multidbapp.model.GlobalResponse;
import org.example.multidbapp.model.user.UserResponse;
import org.example.multidbapp.service.DataSourceCache;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final DataSourceCache dataSourceCache;
    @Override
    public GlobalResponse<List<UserResponse>> getAllUsers() {
        log.info("Try to get all users");
        List<UserResponse> userResponses = new ArrayList<>();
        dataSourceCache.dataSourceFullResponses().forEach(dataSourceFullResponse -> {
            DataSource dataSource = dataSourceFullResponse.dataSource();
            DataSourceProperty dataSourceProperty = dataSourceFullResponse.dataSourceProperty();
            DataSourceMappingProperty dataSourcePropertyMapping = dataSourceProperty.mapping();
            String query = String.format(
                    """
                    SELECT %s 
                    FROM %s u 
                    WHERE u.%s>0
                    """,
                    getSelectFields(dataSourcePropertyMapping),
                    dataSourceProperty.table(),
                    dataSourcePropertyMapping.id()
            );
            addUsersFromDb(userResponses, dataSource, dataSourcePropertyMapping, query);
        });
        log.info("Success in get all users");
        return new GlobalResponse<>(true, userResponses);
    }

    @Override
    public GlobalResponse<List<UserResponse>> getAllUsersWithFilter(String filter) {
        log.info("Try to get all users with filter:{}", filter);
        List<UserResponse> userResponses = new ArrayList<>();
        dataSourceCache.dataSourceFullResponses().forEach(dataSourceFullResponse -> {
            DataSource dataSource = dataSourceFullResponse.dataSource();
            DataSourceProperty dataSourceProperty = dataSourceFullResponse.dataSourceProperty();
            DataSourceMappingProperty dataSourcePropertyMapping = dataSourceProperty.mapping();
            String query = String.format(
                    """
                    SELECT %s 
                    FROM %s u 
                    WHERE 
                    %s or (
                        u.%s LIKE '%%%s%%' or 
                        u.%s LIKE '%%%s%%' or
                        u.%s LIKE '%%%s%%'
                    )
                    """,
                    getSelectFields(dataSourcePropertyMapping),
                    dataSourceProperty.table(),
                    filter == null,
                    dataSourcePropertyMapping.username(),
                    filter,
                    dataSourcePropertyMapping.name(),
                    filter,
                    dataSourcePropertyMapping.surname(),
                    filter
            );
            addUsersFromDb(userResponses, dataSource, dataSourcePropertyMapping, query);
        });
        log.info("Success in get all users with filter");
        return new GlobalResponse<>(true, userResponses);
    }

    private void addUsersFromDb(List<UserResponse> userResponses, DataSource dataSource, DataSourceMappingProperty dataSourcePropertyMapping, String query){
        log.info("Sql query: {}", query);
        try(
                Connection connection = dataSource.getConnection();
                ResultSet rs = connection.prepareStatement(query).executeQuery();
        ){
            while (rs.next()){
                userResponses.add(new UserResponse(rs.getLong(dataSourcePropertyMapping.id()), rs.getString(dataSourcePropertyMapping.username()), rs.getString(dataSourcePropertyMapping.name()), rs.getString(dataSourcePropertyMapping.surname())));
            }
        }catch (SQLException e){
            log.error("Error in get users", e);
        }
    }

    private String getSelectFields(DataSourceMappingProperty dataSourceMappingProperty){
        return String.format("u.%s, u.%s, u.%s, u.%s", dataSourceMappingProperty.id(), dataSourceMappingProperty.username(), dataSourceMappingProperty.name(), dataSourceMappingProperty.surname());
    }
}
