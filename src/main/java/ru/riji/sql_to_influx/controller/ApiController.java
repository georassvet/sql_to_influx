package ru.riji.sql_to_influx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.riji.sql_to_influx.dao.SqlTaskDao;

@RestController
public class ApiController {
    @Autowired
    private SqlTaskDao sqlTaskDao;

    @GetMapping("/api/tasks")
    private ResponseEntity<?> tasks() {
        return new ResponseEntity<>(sqlTaskDao.getAll(), HttpStatus.OK);
    }


}
