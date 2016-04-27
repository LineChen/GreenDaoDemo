package com.beiing.greendaodemo.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by chenliu on 2016/4/27.<br/>
 * 描述：
 * </br>
 */
public abstract class AbstractMigratorHelper {

    public abstract void onUpgrade(SQLiteDatabase db);
}