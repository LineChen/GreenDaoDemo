package com.beiing.greendaodemo;

import android.app.Application;

import com.beiing.greendaodemo.db.AbstractDatabaseManager;

/**
 * Created by chenliu on 2016/4/27.<br/>
 * 描述：
 * </br>
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化数据库管理器
        AbstractDatabaseManager.initOpenHelper(this);
    }
}
