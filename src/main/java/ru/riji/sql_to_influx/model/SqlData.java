package ru.riji.sql_to_influx.model;

import lombok.Data;

import java.util.List;

@Data
public class SqlData {
    private String[] columnNames;
    private String[] columnTypes;
    private String[] influxTypes;
    private List<List<String>> rows;

    public SqlData(String[] columnNames, String[] columnTypes, String[] influxTypes, List<List<String>> rows) {
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        this.influxTypes = influxTypes;
        this.rows = rows;
    }
}
