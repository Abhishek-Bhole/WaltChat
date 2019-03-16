package myapp.abhishek.chatapplication;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String NotificationTitle = remoteMessage.getNotification().getTitle();
        String NotificationMessage = remoteMessage.getNotification().getBody();

        NotificationCompat.Builder mBuilder=
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(NotificationTitle)
                .setContentText(NotificationMessage);

        int mNotificationID = (int)System.currentTimeMillis();

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationID,mBuilder.build());
    }
}
