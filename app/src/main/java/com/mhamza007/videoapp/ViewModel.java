package com.mhamza007.videoapp;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mhamza007.videoapp.db.Database;
import com.mhamza007.videoapp.db.User;
import com.mhamza007.videoapp.db.UserDao;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private static final String TAG = "ViewModel";
    private UserDao userDao;
    private Database dB;
    private List<User> allUsers;

    public ViewModel(@NonNull Application application) {
        super(application);

        dB = Database.getDatabase(application);
        userDao = dB.userDao();
//        allUsers = userDao.getAllUsers();
    }

    public void registerNewUser(User user) {
        new InsertAsyncTask(userDao).execute(user);
    }

    public void update(User user) {
        new UpdateAsyncTask(userDao).execute(user);
    }

    public List<User> getAllUsers() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                allUsers = userDao.getAllUsers();
            }
        });
        return allUsers;
//        new GetAsyncTask(userDao).execute();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public class GetAsyncTask extends AsyncTask<User, Void, List<User>> {

        UserDao userDao;

        public GetAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected List<User> doInBackground(User... users) {
            return userDao.getAllUsers();
        }

        @Override
        protected void onPostExecute(List<User> users) {
            getAllUsers();
        }
    }

    private static class InsertAsyncTask extends AsyncTask<User, Void, Void> {

        UserDao userDao;

        public InsertAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.registerUser(users[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<User, Void, Void> {

        UserDao userDao;

        public UpdateAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.updateUser(users[0]);
            return null;
        }
    }
}
