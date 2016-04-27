package com.beiing.greendaodemo.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.be.greendao.DaoMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by chenliu on 2016/4/27.<br/>
 * 描述：数据迁移
 * </br>
 */
public class MigrationHelper {
    private static final String CONVERSION_CLASS_NOT_FOUND_EXCEPTION = "MIGRATION HELPER - CLASS DOESN'T MATCH WITH THE CURRENT PARAMETERS";
    private static MigrationHelper instance;

    public static MigrationHelper getInstance() {
        if(instance == null) {
            instance = new MigrationHelper();
        }
        return instance;
    }

    public void migrate(SQLiteDatabase db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        generateTempTables(db, daoClasses);
        DaoMaster.dropAllTables(db, true);
        DaoMaster.createAllTables(db, false);
        restoreData(db, daoClasses);
    }

    private void generateTempTables(SQLiteDatabase db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for(int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);

            String divider = "";
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            ArrayList<String> properties = new ArrayList<>();

            StringBuilder createTableStringBuilder = new StringBuilder();

            createTableStringBuilder.append("CREATE TABLE ").append(tempTableName).append(" (");

            for(int j = 0; j < daoConfig.properties.length; j++) {
                String columnName = daoConfig.properties[j].columnName;
                    properties.add(columnName);
                    String type = null;
                    try {
                        type = getTypeByClass(daoConfig.properties[j].type);
                    } catch (Exception exception) {
                    }
                    createTableStringBuilder.append(divider).append(columnName).append(" ").append(type);
                    if(daoConfig.properties[j].primaryKey) {
                        createTableStringBuilder.append(" PRIMARY KEY");
                    }
                    divider = ",";
            }
            //--------------------------------创建临时表-----------
            createTableStringBuilder.append(");");
            Log.e("migrate", "createTmpTable:" + createTableStringBuilder.toString());
            db.execSQL(createTableStringBuilder.toString());

            //--------------------------------转移数据-------------
            if(isTableExists(db, tableName)){

                for(int j = 0; j < daoConfig.properties.length; j++) {
                    String columnName = daoConfig.properties[j].columnName;
                    if(!getColumns(db, tableName).contains(columnName)){
                        properties.remove(columnName);
                    }
                }

                StringBuilder insertTableStringBuilder = new StringBuilder();
                insertTableStringBuilder.append("INSERT INTO ").append(tempTableName).append(" (");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(") SELECT ");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(" FROM ").append(tableName).append(";");

                Log.e("migrate", "insertTmpTable:" + insertTableStringBuilder.toString());
                db.execSQL(insertTableStringBuilder.toString());
            }
        }
    }

    private void restoreData(SQLiteDatabase db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for(int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);

            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            ArrayList<String> properties = new ArrayList();

            for (int j = 0; j < daoConfig.properties.length; j++) {
                String columnName = daoConfig.properties[j].columnName;

                if(getColumns(db, tempTableName).contains(columnName)) {
                    properties.add(columnName);
                }
            }

            StringBuilder insertTableStringBuilder = new StringBuilder();

            insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
            insertTableStringBuilder.append(TextUtils.join(",", properties));
            insertTableStringBuilder.append(") SELECT ");
            insertTableStringBuilder.append(TextUtils.join(",", properties));
            insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");

            StringBuilder dropTableStringBuilder = new StringBuilder();

            dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);


            Log.e("migrate", "restoreData:" + insertTableStringBuilder.toString());
            db.execSQL(insertTableStringBuilder.toString());
            db.execSQL(dropTableStringBuilder.toString());
        }
    }

    private String getTypeByClass(Class<?> type) throws Exception {
        if(type.equals(String.class)) {
            return "TEXT";
        }
        if(type.equals(Long.class) || type.equals(Integer.class) || type.equals(long.class)) {
            return "INTEGER";
        }
        if(type.equals(Boolean.class)) {
            return "BOOLEAN";
        }

        Exception exception = new Exception(CONVERSION_CLASS_NOT_FOUND_EXCEPTION.concat(" - Class: ").concat(type.toString()));
        throw exception;
    }

    private static List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 1", null);
            if (cursor != null) {
                columns = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
            }
        } catch (Exception e) {
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return columns;
    }



    /**
     * 判断一个表是否存在
     *
     * @param tableName
     * @return
     *         "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name = ? "
     *         ;
     */
    public static boolean isTableExists(SQLiteDatabase db, String tableName) {
        String sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ? ";
        String paras[] = { tableName };
        try {
            Cursor cursor = db.rawQuery(sql, paras);
            cursor.moveToNext();
            String name = cursor.getString(cursor.getColumnIndex("name"));
            if (name.equals(tableName)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
