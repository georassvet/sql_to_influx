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

import javax.servlet.http.HttpServletRequest;
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
    @PostMapping(value = {"/start"})
    public ResponseEntity<?> start( @RequestParam("id") Integer id){
        service.startById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    @PostMapping(value = {"/stop"})
    public ResponseEntity<?> stop( @RequestParam("id") Integer id){
        service.stopById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    @PostMapping(value = {"/clone"})
    public ResponseEntity<?> clone( @RequestParam("id") Integer id){
        service.clone(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    @PostMapping(value = {"/delete"})
    public ResponseEntity<?> delete( @RequestParam("id") Integer id){
        service.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    @PostMapping(value = {"/test"})
    public ResponseEntity<?> test(Model model, SqlTaskForm form) throws SQLException {
        System.out.println("test");
        return new ResponseEntity<>(service.test(form), HttpStatus.OK);
    }
}
