package ru.riji.sql_to_influx.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.riji.sql_to_influx.form.SqlTaskForm;
import ru.riji.sql_to_influx.helpers.DbUtils;
import ru.riji.sql_to_influx.mappers.SqlTaskMapper;
import ru.riji.sql_to_influx.model.SqlTask;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SqlTaskDao implements IDAO<SqlTask, SqlTaskForm> {

    @Autowired
    private SqlTaskMapper mapper;

    @Override
    public List<SqlTask> getAll() {
        List<SqlTask> items = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(SqlTaskMapper.sql_all)
        ){
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                items.add(mapper.map(rs));
            }
        }catch (SQLException e){
            System.out.println(e);
        }
        return items;
    }

    @Override
    public SqlTask getById(int id) {

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(SqlTaskMapper.sql_get_id)
        ){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
               return mapper.map(rs);
            }
        }catch (SQLException e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public void add(SqlTaskForm form) {
        String sql= "insert into sql_task(name, group_name, query, db_id, influx_id, influx_db, influx_table, interval) values (?,?,?,?,?,?,?,?)";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, form.getName());
            statement.setString(2, form.getGroupName());
            statement.setString(3, form.getQuery());
            statement.setInt(4, form.getConnectionId());
            statement.setInt(5, form.getInfluxConnectionId());
            statement.setString(6,form.getInfluxDatabase());
            statement.setString(7,form.getInfluxMeasurement());
            statement.setLong(8, form.getInterval());
            int count = statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(SqlTaskForm form) {
        String sql= "update sql_task set name=?, query=? db_id=?, influx_id=?, influx_db=?, influx_table=?, interval=?";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, form.getName());
            statement.setString(2, form.getQuery());
            statement.setInt(3, form.getConnectionId());
            statement.setInt(4, form.getInfluxConnectionId());
            statement.setString(5,form.getInfluxDatabase());
            statement.setString(6,form.getInfluxMeasurement());
            statement.setLong(7, form.getInterval());
            statement.executeQuery();

        }catch (SQLException e){

        }
    }

    @Override
    public void delete(int id) {

    }
}
