package com.yordan.finance;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import com.yordan.finance.data.repository.Repository;
import com.yordan.finance.model.Category;
import com.yordan.finance.utils.CategoriesUtils;

import java.util.List;

public class App extends Application {

    private final String TAG = this.getClass().getSimpleName();
    public static final String CHANNEL_1_ID = "Channel 1";
    public static final String REMINDER_NOTIFICATION_WORK_TAG = "notificationWork";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        createNotificationChannel();
        CategoriesUtils.initCategories(this);
        PreferenceManager.setDefaultValues(this, R.xml.root_settings, false);
    }

    public static Context getContext(){
        return context;
    }

    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,
                    "Reminder Notification", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Used to remind user to enter his expenses on a pre-determined interval.");


            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel1);
        }
    }
}
