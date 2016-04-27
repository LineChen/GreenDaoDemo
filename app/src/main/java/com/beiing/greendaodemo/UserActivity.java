package com.beiing.greendaodemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;

import com.be.greendao.Book;
import com.be.greendao.BookDao;
import com.be.greendao.User;
import com.be.greendao.UserDao;
import com.beiing.greendaodemo.db.AbstractDatabaseManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.Query;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }

    public void addUser(View view) {
        User user = new User();
        user.setName("叶晓晓");
        user.setAge(22);
        UserDao userDao = (UserDao) new AbstractDatabaseManager<User, Long>() {
            @Override
            public AbstractDao<User, Long> getAbstractDao() {
                Log.e("==", "daoSession == null" + (daoSession == null));
                return daoSession.getUserDao();
            }
        }.getAbstractDao();
        userDao.insert(user);

////-------------------------------------------------------------------
//        Book book = new Book();
//        book.setName("平凡的世界");
//        book.setPrice(20f);
//
//        BookDao bookDao = (BookDao) new AbstractDatabaseManager<Book, Long>() {
//            @Override
//            public AbstractDao<Book, Long> getAbstractDao() {
//                return daoSession.getBookDao();
//            }
//        }.getAbstractDao();
//        bookDao.insert(book);

    }

    public void searchUser(View view) {

        Query<User> query = new AbstractDatabaseManager<User, Long>() {
            @Override
            public AbstractDao<User, Long> getAbstractDao() {
                return daoSession.getUserDao();
            }
        }.getQueryBuilder()
                .where(UserDao.Properties.Age.eq(22))
                .build();

        List<User> list = query.list();
        Log.e("===", "users:" + Arrays.toString(list.toArray()));

////-------------------------------------------------------------------
//        Query<Book> query2 = new AbstractDatabaseManager<Book, Long>() {
//            @Override
//            public AbstractDao<Book, Long> getAbstractDao() {
//                return daoSession.getBookDao();
//            }
//        }.getQueryBuilder()
//                .where(BookDao.Properties.Name.like("%平凡%"))
//                .build();
//        List<Book> list1 = query2.list();
//        Log.e("===", "books:" + Arrays.toString(list1.toArray()));


    }
}
