package com.fabb.notifica;


import com.orm.SugarRecord;

public class Student extends SugarRecord<Teacher> {
    public String userId;
    public String name;
    public int roll;
    public int batch;
    public int privilege;
    public String groups;

    Faculty faculty;
}