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
        String movieTitle = intent.getStringExtra("MOVIE_TITLE");
        String showtime = intent.getStringExtra("SHOWTIME");
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android 8.0 (Oreo) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for upcoming movie showtimes");
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent to open the app when the notification is tapped
        Intent tapIntent = new Intent(context, HistoryOrder.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, tapIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // Use a mipmap icon
                .setContentTitle("Sắp tới giờ chiếu phim!")
                .setContentText("Phim '" + movieTitle + "' của bạn sẽ bắt đầu lúc " + showtime)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show the notification
        notificationManager.notify(notificationId, builder.build());
    }
}
