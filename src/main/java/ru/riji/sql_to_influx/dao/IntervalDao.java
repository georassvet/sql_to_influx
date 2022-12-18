package ru.riji.sql_to_influx.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.riji.sql_to_influx.form.IntervalForm;
import ru.riji.sql_to_influx.helpers.DbUtils;
import ru.riji.sql_to_influx.mappers.IntervalMapper;
import ru.riji.sql_to_influx.mappers.SqlTaskMapper;
import ru.riji.sql_to_influx.model.Interval;
import ru.riji.sql_to_influx.model.SqlTask;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IntervalDao implements IDAO<Interval, IntervalForm> {
    @Autowired
    private IntervalMapper mapper;

    @Override
    public List<Interval> getAll() {
        List<Interval> items = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(IntervalMapper.sql_all)
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
    public Interval getById(int id) {
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(IntervalMapper.sql_get_id)
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
    public void add(IntervalForm form) {
        String sql= "insert into interval(name, value) values (?,?)";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, form.getName());
            statement.setLong(2, form.getValue());
            int count = statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(IntervalForm intervalForm) {

    }

    @Override
    public void delete(int id) {

    }
}
