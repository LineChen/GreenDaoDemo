package com.be.greendao;

import com.beiing.greendaodemo.db.AbstractDatabaseManager;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by chenliu on 2016/4/27.<br/>
 * 描述：
 * </br>
 */
public class NoteDaoManager extends AbstractDatabaseManager<Note, Long> {
    @Override
    public AbstractDao<Note, Long> getAbstractDao() {
        return daoSession.getNoteDao();
    }
}
