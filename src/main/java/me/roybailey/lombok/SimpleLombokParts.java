package me.roybailey.lombok;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Demonstration of Lombok annotations
 */
@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC)
@ToString(includeFieldNames = true)
public class SimpleLombokParts {

    private String name;
    private Integer age;
    @Setter(AccessLevel.PROTECTED) private Date dob;
}
