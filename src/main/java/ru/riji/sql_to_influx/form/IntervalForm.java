package ru.riji.sql_to_influx.form;

import lombok.Data;

@Data
public class IntervalForm {
    private int id;
    private String name;
    private long value;
}
