package com.fabb.notifica;


import com.orm.SugarRecord;

public class Subject extends SugarRecord<Subject> {
    public String code;     // int is faster than string for searching hence separate primary key and subject code
    public String name;

    Faculty faculty;

}
