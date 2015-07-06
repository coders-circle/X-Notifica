package com.fabb.notifica;

import com.orm.SugarRecord;

public class Teacher extends SugarRecord<Teacher> {
    public String name;
    public String userId;
    public String contact;

    public Faculty faculty;
}

