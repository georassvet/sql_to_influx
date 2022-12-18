package ru.riji.sql_to_influx.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.riji.sql_to_influx.model.SqlTask;

import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class SqlTaskMapper implements IMapper<SqlTask> {

    public static String sql_all= "select st.id id, st.name name, st.group_name group_name, st.query query, st.enable enable, db.id db_id, db.name db_name, db.url db_url, db.user db_user, db.pass db_pass, i.id influx_id, i.name influx_name, i.url influx_url, i.user influx_user, i.pass influx_pass, influx_db, influx_table, st.interval interval from sql_task st " +
        " join db on st.db_id = db.id " +
        " join influx i on st.influx_id = i.id";
    public static String sql_get_id= sql_all + " where st.id=?";

    @Override
    public SqlTask map(ResultSet rs) throws SQLException {
        return new SqlTask(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("group_name"),
                rs.getString("query"),
                rs.getBoolean("enable"),
                rs.getInt("db_id"),
                rs.getString("db_name"),
                rs.getString("db_url"),
                rs.getString("db_user"),
                rs.getString("db_pass"),
                rs.getInt("influx_id"),
                rs.getString("influx_name"),
                rs.getString("influx_url"),
                rs.getString("influx_user"),
                rs.getString("influx_pass"),
                rs.getString("influx_db"),
                rs.getString("influx_table"),
                rs.getLong("interval")
        );
    }
}
