package ru.riji.sql_to_influx.service;

import java.sql.SQLException;

public interface IService<T,R,F> {
    void runAll();
    void stopAll();
    boolean startById(int id);
    boolean stopById(int id);
    int clone(int id);
    void delete(int id);
    R test(F f) throws SQLException;
    void add(F f);
    void update(F f);
}
