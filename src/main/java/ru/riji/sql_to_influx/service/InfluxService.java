package ru.riji.sql_to_influx.service;

import okhttp3.*;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.stereotype.Service;
import ru.riji.sql_to_influx.model.Connect;
import ru.riji.sql_to_influx.model.SqlData;
import ru.riji.sql_to_influx.tasks.SqlTask;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class InfluxService {

    private static OkHttpClient client = new OkHttpClient();
    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    private static  DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .appendPattern("[.SSSSSSSSS][.SSSSSS][.SSS]")
            .toFormatter();


        private static final ThreadLocal<SimpleDateFormat> FORMATTER_MILLIS = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                SimpleDateFormat dateDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                dateDF.setTimeZone(TimeZone.getTimeZone("UTC"));
                return dateDF;
            }
        };

    private static String formatedTime(Number time, final TimeUnit precision) {
        if (time == null) {
            return null;
        }
        TimeUnit converterPrecision = precision;

        if (converterPrecision == null) {
            converterPrecision = TimeUnit.NANOSECONDS;
        }
        if (time instanceof BigInteger) {
            BigInteger time2 = (BigInteger) time;
            long conversionFactor = converterPrecision.convert(1, precision);
            if (conversionFactor >= 1) {
                time = time2.multiply(BigInteger.valueOf(conversionFactor));
            } else {
                conversionFactor = precision.convert(1, converterPrecision);
                time = time2.divide(BigInteger.valueOf(conversionFactor));
            }
            return time.toString();
        } else if (time instanceof BigDecimal) {
            BigDecimal time2 = (BigDecimal) time;
            long conversionFactor = converterPrecision.convert(1, precision);
            if (conversionFactor >= 1) {
                time = time2.multiply(BigDecimal.valueOf(conversionFactor));
            } else {
                conversionFactor = precision.convert(1, converterPrecision);
                time = time2.divide(BigDecimal.valueOf(conversionFactor), RoundingMode.HALF_UP);
            }
            return time.toString();
        } else {
            return Long.toString(converterPrecision.convert(time.longValue(), precision));
        }
    }


    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private static InfluxDB buildConnection(Connect connect, String dbName) {
        InfluxDB influxDB = null;
        influxDB= InfluxDBFactory.connect(connect.getUrl(), connect.getUser(), connect.getPass());
        influxDB.setDatabase(dbName);
        influxDB.setRetentionPolicy("autogen");
        influxDB.enableBatch(2000, 1000, TimeUnit.MILLISECONDS);

        return influxDB;
    }

//    public static void writeData(SqlData data, SqlTask task){
//        System.out.println("influx");
//        InfluxDB influxDB = buildConnection(task.getInfluxConnect(), task.getInfluxDatabase());
//        BatchPoints points = doPoints(task.getInfluxTable(),task.getInfluxDatabase(), data);
//        influxDB.write(points);
//        influxDB.close();
//        System.out.println("influx close");
//    }

    public static String toInfluxDBTimeFormat(final Instant time) {
        return DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.ofInstant(time, ZoneId.of("UTC").normalized()));
    }
    public static void writeLineFormat2(SqlData data, SqlTask task) throws IOException, URISyntaxException, ParseException {
        String url = String.format("%s/write?db=%s",
                task.getInfluxConnect().getUrl(),
                task.getInfluxDatabase());
         StringBuilder body = new StringBuilder();
            for(int i=0; i < data.getRows().size(); i++) {
                StringBuilder tags = new StringBuilder();
                StringBuilder fields = new StringBuilder();
                String time = "";
                for (int j = 0; j < data.getInfluxTypes().length; j++) {
                    switch (data.getInfluxTypes()[j]) {
                        case "time": {
                             LocalDateTime localDateTime = LocalDateTime.parse(data.getRows().get(i).get(j), formatter);
                            //ZoneId zoneId = ZoneId.of("America/Chicago");
                            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
                            Instant instant = zonedDateTime.toInstant();

                          //  Instant instant = Instant.parse("2023-04-12T22:27:00.00Z");
                            long epoch = instant.getEpochSecond()  * 1000000000 + instant.getNano();
                            time = Long.toString(epoch);// formatedTime(dt.getMillis(), TimeUnit.NANOSECONDS);
                            break;
                        }
                        case "tag": {
                            if (tags.length() > 0) {
                                tags.append(",");
                            }
                            tags.append(data.getColumnNames()[j]).append("=").append(data.getRows().get(i).get(j));
                            break;
                        }
                        case "field": {
                            if (fields.length() > 0) {
                                fields.append(",");
                            }
                            switch (data.getColumnTypes()[j]) {
                                case "int4":
                                case "int8":
                                case "int2":
                                case "int":
                                case "serial8":
                                case "serial4":
                                case "serial2": {
                                    fields.append(data.getColumnNames()[j]).append("=").append(Long.parseLong(data.getRows().get(i).get(j)));
                                    break;
                                }
                                case "float4":
                                case "float8":
                                case "numeric":
                                case "decimal": {
                                    fields.append(data.getColumnNames()[j]).append("=").append(Double.parseDouble(data.getRows().get(i).get(j)));
                                    break;
                                }
                                default: {
                                    fields.append(data.getColumnNames()[j]).append("=").append(data.getRows().get(i).get(j));
                                    break;
                                }
                            }
                        }
                    }
                }
                String format = String.format("%s,%s %s %s\n", task.getInfluxTable(), tags, fields, time);
                body.append(format);
            }
            
            RequestBody requestBody =  RequestBody.create(body.toString(),MediaType.parse("text/plain"));

            Request request = new Request.Builder()
                .url(url)
                    .addHeader("Authorization", String.format("Token %s:%s", task.getInfluxConnect().getUser(),task.getInfluxConnect().getPass()))
                    .post(requestBody)
                .build();

            Response response = client.newCall(request).execute();
            response.close();
            System.out.println(String.format("%s %s\n%s", response.code(), task.getInfluxDatabase(), body));
    }
//    private static BatchPoints doPoints(String measurement, String dbName, SqlData data){
//
//        BatchPoints points = BatchPoints.database(dbName).build();
//
//        for (int i = 0; i < data.getRows().size() ; i++) {
//
//            Point.Builder point = Point.measurement(measurement);
//            for (int j = 0; j < data.getRows().get(i).size(); j++) {
//                if (data.getInfluxTypes()[j] != null) {
//                    switch (data.getInfluxTypes()[j]) {
//                        case "TIME": {
//                            point.time(LocalDateTime.parse(data.getRows().get(i).get(j), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
//                                    ).atZone(ZoneId.systemDefault())
//                                    .toInstant().toEpochMilli(), TimeUnit.MILLISECONDS);
//                            break;
//                        }
//                        case "TAG": {
//                            point.tag(data.getColumnNames()[j], data.getRows().get(i).get(j));
//                            break;
//                        }
//                        case "FIELD": {
//                            switch (data.getColumnTypes()[j]) {
//                                case "int4":
//                                case "int8":
//                                case "int2":
//                                case "int":
//                                case "serial8":
//                                case "serial4":
//                                case "serial2": {
//                                    point.addField(data.getColumnNames()[j], Long.parseLong(data.getRows().get(i).get(j)));
//                                    break;
//                                }
//                                case "float4":
//                                case "float8":
//                                case "numeric":
//                                case "decimal": {
//                                    point.addField(data.getColumnNames()[j], Double.parseDouble(data.getRows().get(i).get(j)));
//                                    break;
//                                }
//                                default: {
//                                    point.addField(data.getColumnNames()[j], data.getRows().get(i).get(j));
//                                    break;
//                                }
//                            }
//                            break;
//                        }
//                    }
//                }
//            }
//            points.point(point.build());
//        }
//        return points;
//    }



}
