package ru.riji.sql_to_influx.helpers;


import lombok.Data;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


@Data
@Service
public class DbUtils {
    private static final int connectionSize = 20;
    private static String url;

    private BlockingQueue<Connection> connections = new ArrayBlockingQueue<>(connectionSize);


    private String table_sql_task = "CREATE TABLE IF NOT EXISTS sql_task (\n" +
            "    id                INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name              CHAR,\n" +
            "    query           CHAR,\n" +
            "    description           CHAR,\n" +
            "    interval          BIGINT,\n" +
            "    enable            BOOLEAN,\n" +
            "    db_id        INTEGER REFERENCES db (id),\n" +
            "    influx_table      CHAR,\n" +
            "    influx_db         CHAR,\n" +
            "    influx_id INTEGER REFERENCES influx (id),\n" +
            "    group_name        CHAR\n" +
            ")";
    private String table_connect = "CREATE TABLE IF NOT EXISTS db (\n" +
            "    id     INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name   CHAR,\n" +
            "    url    CHAR,\n" +
            "    user   CHAR,\n" +
            "    pass   CHAR\n" +
            ")";
    private String table_influx_connect = "CREATE TABLE IF NOT EXISTS influx (\n" +
            "    id          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name CHAR,\n" +
            "    url  CHAR,\n" +
            "    user CHAR,\n" +
            "    pass CHAR\n" +
            ")";
    private String table_interval = "CREATE TABLE IF NOT EXISTS interval (\n" +
            "    id          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name CHAR,\n" +
            "    value  BIGINT\n" +
            ")";

    private String insert_interval = "insert into interval(name, value) values  " +
            " ('1 second', 1000)," +
            " ('5 seconds', 5000)," +
            " ('10 seconds', 10000)," +
            " ('30 seconds', 30000)," +
            " ('1 minute', 60000)," +
            " ('5 minutes', 300000)," +
            " ('10 minutes', 600000)," +
            " ('30 minute', 1800000),"+
            " ('1 hour', 3600000)," +
            " ('24 hours', 86400000);";

    public static String getUrl() {
        return url;
    }

    @EventListener(ApplicationReadyEvent.class)
    void init() throws SQLException, IOException {
        String path= "";
        if(System.getProperty("os.name").contains("Windows")) {
            path = "C:/sqlite/db";
        }else {
            path = "./sqlite/db";
        }
        Path dbPath = Paths.get(path);
        if(Files.notExists(dbPath)) {
            Files.createDirectories(dbPath);
            System.out.println("dir created");
        }

        url = "jdbc:sqlite:" + path+"/sqlite.db";

        updateDb(table_connect);
        updateDb(table_influx_connect);
        updateDb(table_sql_task);
        updateDb(table_interval);
       // updateDb(insert_interval);
    }

    private void updateDb(String query){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement()){
            statement.execute(query);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
