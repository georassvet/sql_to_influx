package ru.riji.sql_to_influx.model;

import lombok.Data;

@Data
public class Interval implements ITask {
    private int id;
    private String name;
    private long value;

    public Interval(int id, String name, long value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }
}
