package ru.riji.sql_to_influx.tasks;

import lombok.Data;
import lombok.SneakyThrows;
import ru.riji.sql_to_influx.model.Connect;
import ru.riji.sql_to_influx.model.SqlData;
import ru.riji.sql_to_influx.runner.SqlRunner;
import ru.riji.sql_to_influx.service.InfluxService;

import java.util.concurrent.ScheduledFuture;

@Data
public class SqlTask implements ITask, Runnable {
    private int id;
    private String name;
    private String groupName;
    private String query;
    private String description;
    private boolean enable;
    private Connect dbConnect;
    private Connect influxConnect;
    private String influxDatabase;
    private String influxTable;
    private long interval;

    private ScheduledFuture<?> scheduledFuture;

    public SqlTask(int id, String name, String groupName, String query, boolean enable, int dbConnectId, String dbConnectName, String dbConnectUrl, String dbConnectUser, String dbConnectPass, int influxConnectId, String influxConnectName, String influxConnectUrl, String influxConnectUser, String influxConnectPass, String influxDatabase, String influxTable, long interval, String description) {
        this.id = id;
        this.name = name;
        this.groupName = groupName;
        this.query = query;
        this.enable = enable;
        this.dbConnect = new Connect(dbConnectId,dbConnectName,dbConnectUrl,dbConnectUser,dbConnectPass);
        this.influxConnect = new Connect(influxConnectId,influxConnectName,influxConnectUrl,influxConnectUser,influxConnectPass);
        this.influxDatabase = influxDatabase;
        this.influxTable = influxTable;
        this.interval = interval;
        this.description = description;
    }

    @SneakyThrows
    @Override
    public void run() {
        SqlData data = SqlRunner.runCommand(this.dbConnect, this.getQuery());
        InfluxService.writeLineFormat2(data, this);
    }
}
