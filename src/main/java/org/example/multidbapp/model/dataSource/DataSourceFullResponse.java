package org.example.multidbapp.model.dataSource;

import javax.sql.DataSource;

public record DataSourceFullResponse(
        DataSource dataSource,
        DataSourceProperty dataSourceProperty
) {
}
