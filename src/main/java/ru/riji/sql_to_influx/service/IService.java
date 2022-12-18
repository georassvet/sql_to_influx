package ru.riji.sql_to_influx.service;

public interface IService<T,F> {
    void runAll();
    void stopAll();
    boolean startById(int id);
    boolean stopById(int id);
    int clone(int id);
    void delete(int id);
    void add(F f);
    void update(F f);
}
