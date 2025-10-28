package com.example.prm392_cinema.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.prm392_cinema.HistoryOrder;
import com.example.prm392_cinema.R;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "SHOWTIME_REMINDER_CHANNEL";
    private static final String CHANNEL_NAME = "Showtime Reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("TITTLE");
        String message = intent.getStringExtra("MESSAGE");
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);

        // Fallback in case the extras are missing
        if (title == null || title.isEmpty()) {
            title = "Thông báo từ PRM392_Cinema";
        }
        if (message == null || message.isEmpty()) {
            message = "Bạn có một lời nhắc mới.";
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for showtime reminders and payment alerts");
            notificationManager.createNotificationChannel(channel);
        }

        Intent tapIntent = new Intent(context, HistoryOrder.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, tapIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationId, builder.build());
    }
}
