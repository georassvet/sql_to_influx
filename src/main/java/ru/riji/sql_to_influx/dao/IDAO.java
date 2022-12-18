package ru.riji.sql_to_influx.dao;

import java.util.List;

 public interface IDAO<T,F> {
   List<T> getAll();
   T getById(int id);
   void add(F f);
   void update(F f);
   void delete(int id);
}
