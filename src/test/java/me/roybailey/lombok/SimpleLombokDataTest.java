package me.roybailey.lombok;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

public class SimpleLombokDataTest {

    @Test
    public void testLombokData() {
        SimpleLombokData record = new SimpleLombokData();
        record.setName("Wife v1.0");
        record.setAge(40);
        LocalDate dob = LocalDate.of(1975, Month.DECEMBER, 25);
        record.setDob(new Date(dob.toEpochDay()));
        System.out.println(record);
    }
}
