package me.roybailey.lombok;

import lombok.Value;

import java.util.Date;

@Value(staticConstructor = "getInstance")
public class SimpleLombokValue {

    private String name;
    private Integer age;
    private Date dob;
}
