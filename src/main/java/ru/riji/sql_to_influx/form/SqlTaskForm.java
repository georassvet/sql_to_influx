package ru.riji.sql_to_influx.form;

import lombok.Data;
import ru.riji.sql_to_influx.model.IForm;
import ru.riji.sql_to_influx.model.SqlTask;

@Data
public class SqlTaskForm implements IForm {
    private int id;
    private String name;
    private String groupName;
    private long interval;
    private int connectionId;
    private int influxConnectionId;
    private String influxDatabase;
    private String influxMeasurement;
    private String query;
    private String description;

    public SqlTaskForm() {
    }

    public SqlTaskForm(SqlTask task) {
        this.id = task.getId();
        this.name = task.getName();
        this.groupName = task.getGroupName();
        this.interval = task.getInterval();
        this.connectionId = task.getDbConnect().getId();
        this.influxConnectionId = task.getInfluxConnect().getId();
        this.influxDatabase = task.getInfluxDatabase();
        this.influxMeasurement = task.getInfluxTable();
        this.query = task.getQuery();
        this.description = task.getDescription();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public int getInfluxConnectionId() {
        return influxConnectionId;
    }

    public void setInfluxConnectionId(int influxConnectionId) {
        this.influxConnectionId = influxConnectionId;
    }

    public String getInfluxDatabase() {
        return influxDatabase;
    }

    public void setInfluxDatabase(String influxDatabase) {
        this.influxDatabase = influxDatabase;
    }

    public String getInfluxMeasurement() {
        return influxMeasurement;
    }

    public void setInfluxMeasurement(String influxMeasurement) {
        this.influxMeasurement = influxMeasurement;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
