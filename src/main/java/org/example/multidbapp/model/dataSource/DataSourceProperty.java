package org.example.multidbapp.model.dataSource;

public record DataSourceProperty(
    String name,
    String strategy,
    String url,
    String table,
    String user,
    String password,
    DataSourceMappingProperty mapping
) {
}
