package org.example.multidbapp.model.response.dataSource;

import javax.sql.DataSource;

public record DataSourceFullResponse(
        DataSource dataSource,
        DataSourceProperty dataSourceProperty
) {
}
