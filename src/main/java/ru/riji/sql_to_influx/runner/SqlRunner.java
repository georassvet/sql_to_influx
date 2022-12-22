package ru.riji.sql_to_influx.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.riji.sql_to_influx.model.Connect;
import ru.riji.sql_to_influx.model.SqlData;
import ru.riji.sql_to_influx.model.SqlTask;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class SqlRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public static SqlData runCommand(Connect connect, String query) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(
            Connection connection = DriverManager.getConnection(connect.getUrl(), connect.getUser(), connect.getPass());
            PreparedStatement statement = connection.prepareStatement(query)){

            ResultSetMetaData rsmd = statement.getMetaData();
            int columns = rsmd.getColumnCount();
            String[] columnNames = new String[columns];
            String[] columnTypes = new String[columns];
            String[] influxTypes = new String[columns];

            for (int i = 0, j = 0; i < columns ; i++) {
                String columnName =  rsmd.getColumnName(i + 1);
                String tag = parseTag(columnName);
                columnTypes[j] = rsmd.getColumnTypeName( i + 1);
                columnNames[j] = removeTag(columnName, tag);
                if(tag !=null) {
                    influxTypes[j] = tag;
                }
                j++;
            }
            ResultSet rs = statement.executeQuery();

            List<List<String>> rows = new ArrayList<>();

            if(rs.next()){
                do{
                    List<String> row = new ArrayList<>();
                    for(int i = 0; i < columnNames.length; i++){
                       row.add(rs.getString(i+1));
                    }
                    rows.add(row);
                }while (rs.next());
            }

            return new SqlData(columnNames, columnTypes, influxTypes, rows);

        }catch (SQLException e){
            e.printStackTrace();
            return new SqlData(e.getMessage());
        }
    }

    private static String removeTag(String columnName, String tag) {
        return columnName.replaceAll("_" + tag, "");
    }

    private static String parseTag(String columnName) {
        if(columnName.toUpperCase().contains("_TAG")){
            return "TAG";
        }else if(columnName.toUpperCase().contains("_FIELD")){
            return "FIELD";
        }else if(columnName.toUpperCase().contains("_TIME")){
            return "TIME";
        }else {
            return null;
        }
    }


    public static void main(String[] args) {
        LocalDateTime init = LocalDateTime.now();
        LocalDateTime end = init.minusMinutes(1).withSecond(0).withNano(0);
        LocalDateTime start = end.minus(1000, ChronoUnit.MILLIS).withSecond(0);

        System.out.println(start);
        System.out.println(end);
    }
}
