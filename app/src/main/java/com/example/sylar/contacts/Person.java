package com.example.sylar.contacts;

class Person {
    String photoID;
    String name;
    String number;
    int dbId;

    Person(String photoID, String name, String number,int dbId) {
        this.photoID = photoID;
        this.name = name;
        this.number = number;
        this.dbId = dbId;
    }
}