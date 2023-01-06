package ru.riji.sql_to_influx.dao;

import ru.riji.sql_to_influx.model.IForm;
import ru.riji.sql_to_influx.model.ITask;

import java.util.List;

 public interface IDAO<T extends ITask, F extends IForm> {
   List<T> getAll();
   T getById(int id);
   void add(F f);
   void update(F f);
   void update(T t);
   void delete(int id);
}
