package ru.riji.sql_to_influx.service;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;
import ru.riji.sql_to_influx.model.Connect;
import ru.riji.sql_to_influx.model.SqlData;
import ru.riji.sql_to_influx.model.SqlTask;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
public class InfluxService {

    private static InfluxDB buildConnection(Connect connect, String dbName) {
        InfluxDB influxDB = null;
        influxDB= InfluxDBFactory.connect(connect.getUrl(), connect.getUser(), connect.getPass());
        influxDB.setDatabase(dbName);
        influxDB.setRetentionPolicy("autogen");
        influxDB.enableBatch(2000, 1000, TimeUnit.MILLISECONDS);

        return influxDB;
    }

    public static void writeData(SqlData data, SqlTask task){
        System.out.println("influx");
        InfluxDB influxDB = buildConnection(task.getInfluxConnect(), task.getInfluxDatabase());
        BatchPoints points = doPoints(task.getInfluxTable(),task.getInfluxDatabase(), data);
        influxDB.write(points);
        influxDB.close();
        System.out.println("influx close");
    }

    private static BatchPoints doPoints(String measurement, String dbName, SqlData data){

        BatchPoints points = BatchPoints.database(dbName).build();

        for (int i = 0; i < data.getRows().size() ; i++) {

            Point.Builder point = Point.measurement(measurement);
            for (int j = 0; j < data.getRows().get(i).size(); j++) {
                if (data.getInfluxTypes()[j] != null) {
                    switch (data.getInfluxTypes()[j]) {
                        case "TIME": {
                            point.time(LocalDateTime.parse(data.getRows().get(i).get(j), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                                    ).atZone(ZoneId.systemDefault())
                                    .toInstant().toEpochMilli(), TimeUnit.MILLISECONDS);
                            break;
                        }
                        case "TAG": {
                            point.tag(data.getColumnNames()[j], data.getRows().get(i).get(j));
                            break;
                        }
                        case "FIELD": {
                            switch (data.getColumnTypes()[j]) {
                                case "int4":
                                case "int8":
                                case "int2":
                                case "int":
                                case "serial8":
                                case "serial4":
                                case "serial2": {
                                    point.addField(data.getColumnNames()[j], Long.parseLong(data.getRows().get(i).get(j)));
                                    break;
                                }
                                case "float4":
                                case "float8":
                                case "numeric":
                                case "decimal": {
                                    point.addField(data.getColumnNames()[j], Double.parseDouble(data.getRows().get(i).get(j)));
                                    break;
                                }
                                default: {
                                    point.addField(data.getColumnNames()[j], data.getRows().get(i).get(j));
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            points.point(point.build());
        }
        return points;
    }



}
