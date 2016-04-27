package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class NoteDaoGenerator {

    public static void main(String[] args) throws Exception{
        Schema schema = new Schema(1,"com.be.greendao");
        addNote(schema);
        addUser(schema);
        addBook(schema);
        new DaoGenerator().generateAll(schema, "..\\GreenDaoDemo\\app\\src\\main\\java-gen");
    }


    public static void addNote(Schema schema){
        Entity note = schema.addEntity("Note");
        note.addIdProperty();
        note.addStringProperty("text").notNull();
        note.addStringProperty("cotent");
        note.addDateProperty("date");
    }


    public static void addUser(Schema schema){
        Entity user = schema.addEntity("User");
        user.addIdProperty();
        user.addStringProperty("name").notNull();
        user.addIntProperty("age");
        user.addIntProperty("uid");
        user.addStringProperty("sex");
    }

    public static void addBook(Schema schema){
        Entity user = schema.addEntity("Book");
        user.addIdProperty();
        user.addStringProperty("name").notNull();
        user.addFloatProperty("price");
    }

}
