package com.yordan.finance.workersAndTasks;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.yordan.finance.R;
import com.yordan.finance.view.ui.activities.ActivityChooseCategory;

import static com.yordan.finance.App.CHANNEL_1_ID;

public class NotifyWorker extends Worker {

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        triggerNotification(getApplicationContext());
        return Result.success();
    }

    private void triggerNotification(Context context) {         //delay is after how much time(in millis) from current time you want to schedule the notification
        Intent actionWhenClicked = new Intent(context, ActivityChooseCategory.class);
        PendingIntent actionIntent = PendingIntent.getActivity(context, 0, actionWhenClicked, 0);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_no_dolars)
                .setContentTitle("Just a kind reminder")
                .setContentText("You haven't entered any expenses for today.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(actionIntent)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, notification);
    }


}
