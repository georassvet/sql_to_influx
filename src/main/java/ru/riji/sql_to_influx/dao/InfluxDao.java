package ru.riji.sql_to_influx.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.riji.sql_to_influx.form.ConnectForm;
import ru.riji.sql_to_influx.helpers.DbUtils;
import ru.riji.sql_to_influx.mappers.ConnectMapper;
import ru.riji.sql_to_influx.model.Connect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InfluxDao implements IDAO<Connect, ConnectForm> {
    @Autowired
    private ConnectMapper mapper;
    @Override
    public List<Connect> getAll() {
        List<Connect> items = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(ConnectMapper.sql_all)
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
    public Connect getById(int id) {
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(ConnectMapper.sql_get_id)
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
    public void add(ConnectForm form) {
        String sql= "insert into influx(name, url, user, pass) values (?,?,?,?)";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, form.getName());
            statement.setString(2, form.getUrl());
            statement.setString(3, form.getUser());
            statement.setString(4, form.getPass());
            int count = statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(ConnectForm form) {
        String sql= "update influx set name=?, url=?, user=?, pass=? where id =?";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, form.getName());
            statement.setString(2, form.getUrl());
            statement.setString(3, form.getUser());
            statement.setString(4, form.getPass());
            statement.setInt(5, form.getId());
            statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Connect form) {
        String sql= "update influx set name=?, url=?, user=?, pass=? where id =?";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, form.getName());
            statement.setString(2, form.getUrl());
            statement.setString(3, form.getUser());
            statement.setString(4, form.getPass());
            statement.setInt(5, form.getId());
            statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql= "delete from influx where id=?";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setInt(1, id);
            statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
