package ru.riji.sql_to_influx.service;

import lombok.SneakyThrows;
import org.apache.tomcat.util.threads.ScheduledThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ru.riji.sql_to_influx.dao.IDAO;
import ru.riji.sql_to_influx.form.SqlTaskForm;
import ru.riji.sql_to_influx.model.SqlTask;

import java.util.List;

@Service
public class SqlTaskService implements IService<SqlTask,SqlTaskForm> {
   @Autowired
   private IDAO<SqlTask,SqlTaskForm> dao;
   private ThreadPoolTaskScheduler scheduler;
   private List<SqlTask> tasks;

   @SneakyThrows
   @Override
   @EventListener(ApplicationReadyEvent.class)
   public void runAll() {
      List<SqlTask> tasks = dao.getAll();
      for( SqlTask task : tasks){
         if(task.isEnable()) {
            runTask(task);
            Thread.sleep(60 - 60 / tasks.size());
         }
      }
   }

   @Override
   public void stopAll() {

   }
   private SqlTask findTask(int id){
      for (SqlTask task: tasks) {
         if(task.getId() == id){
           return task;
         }
      }
      return null;
   }
   private void runTask(SqlTask task){
      task.setScheduledFuture(scheduler.scheduleWithFixedDelay(task, task.getInterval()));
      tasks.add(task);
   }
   private void runTask(SqlTask task, int delay){
      task.setScheduledFuture(scheduler.scheduleWithFixedDelay(task, task.getInterval()));
      tasks.add(task);
   }


   @Override
   public boolean startById(int id) {
      if(findTask(id)==null){
         SqlTask task = dao.getById(id);
         runTask(task);
      }
      return true;
   }

   @Override
   public boolean stopById(int id) {
      SqlTask task = findTask(id);
      if(task!=null){
         task.getScheduledFuture().cancel(true);
         tasks.remove(task);
         //dao.update();
      }
      return false;
   }

   @Override
   public int clone(int id) {
      return 0;
   }

   @Override
   public void delete(int id) {

   }
   @Override
   public void add(SqlTaskForm form) {
      dao.add(form);
   }

   @Override
   public void update(SqlTaskForm form) {
      dao.update(form);
   }
}
