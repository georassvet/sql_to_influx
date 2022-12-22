package ru.riji.sql_to_influx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.riji.sql_to_influx.dao.SqlTaskDao;
import ru.riji.sql_to_influx.form.SqlTaskForm;
import ru.riji.sql_to_influx.service.SqlTaskService;

import java.sql.SQLException;

@RestController
public class AppController {

    @Autowired
    SqlTaskService service;

    @Autowired
    SqlTaskDao dao;

    @GetMapping(value = {"/tasks"})
    public ResponseEntity<?> tasks(RequestEntity<?> request){
        return new ResponseEntity<>(dao.getAll(), HttpStatus.OK);
    }
    @PostMapping(value = {"/tasks/start/{id}"})
    public ResponseEntity<?> start(RequestEntity<?> request, @PathVariable("id") int id){
        System.out.println(id);
        return new ResponseEntity<>(dao.getAll(), HttpStatus.OK);
    }
    @PostMapping(value = {"/test"})
    public ResponseEntity<?> test(Model model, SqlTaskForm form) throws SQLException {

        return new ResponseEntity<>(service.test(form), HttpStatus.OK);
    }
}
