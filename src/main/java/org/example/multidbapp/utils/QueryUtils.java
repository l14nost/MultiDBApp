package org.example.multidbapp.utils;

import org.example.multidbapp.model.response.dataSource.DataSourceMappingProperty;

public final class QueryUtils {

    private QueryUtils() { }

    public static final String SELECT_FIELDS = "u.%s, u.%s, u.%s, u.%s";
    public static final String GET_ALL_QUERY = """
                    SELECT %s
                    FROM %s u
                    WHERE u.%s>0
                    """;
    public static final String GET_ALL_QUERY_WITH_FILTER = """
                    SELECT %s
                    FROM %s u
                    WHERE
                        LOWER(TRIM(u.%s)) LIKE '%s' or
                        LOWER(TRIM(u.%s)) LIKE '%s' or
                        LOWER(TRIM(u.%s)) LIKE '%s'
                    """;

    public static String getSelectFields(DataSourceMappingProperty dataSourceMappingProperty, String strategy){
        return String.format(SELECT_FIELDS, addQuoteToName(dataSourceMappingProperty.id(), strategy), addQuoteToName(dataSourceMappingProperty.username(), strategy), addQuoteToName(dataSourceMappingProperty.name(), strategy), addQuoteToName(dataSourceMappingProperty.surname(), strategy));
    }

    public static String addQuoteToName(String field, String dbStrategy){
        String quote = dbStrategy.equals("mysql") ? "`" : "\"";
        return quote + field + quote;
    }
}
