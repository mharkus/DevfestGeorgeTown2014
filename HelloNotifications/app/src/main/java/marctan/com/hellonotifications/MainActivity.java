package marctan.com.hellonotifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.ActionBarActivity;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private Bitmap curryNoodles;
    private Bitmap koayTeow;
    private Bitmap oyster;
    private Bitmap mainbg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainbg = BitmapFactory.decodeResource(getResources(), R.drawable.mainbg);
        curryNoodles = BitmapFactory.decodeResource(getResources(), R.drawable.currynoodles);
        koayTeow = BitmapFactory.decodeResource(getResources(), R.drawable.koayteow);
        oyster = BitmapFactory.decodeResource(getResources(), R.drawable.oyster);
    }

    public void showBasicNotification(View v) {
        final Notification notification = createNotificationBuilder().build();
        sendNotification(notification);
    }

    public void showExpandedNotification(View b) {
        final NotificationCompat.Builder builder = createNotificationBuilder();

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] items = {"Penang Laksa", "Char Koay Teow", "Penang Rojak"};
        inboxStyle.setBigContentTitle("Best Sellers");

        for (int i = 0; i < items.length; i++) {

            inboxStyle.addLine(items[i]);
        }

        builder.setStyle(inboxStyle);

        sendNotification(builder.build());
    }

    public void showInvitation(View b) {
        String replyLabel = "What do you think?";

        String[] replyChoices = getResources().getStringArray(R.array.reply_choices);

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .setChoices(replyChoices)
                .build();

        Intent replyIntent = new Intent(this, ReplyActivity.class);
        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(this, 0, replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.sym_action_email,
                        "Reply", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        Notification notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Message from Weng")
                        .setContentText("What can you say about Penang Laksa?")
                        .extend(new NotificationCompat.WearableExtender().addAction(action))
                        .build();

        sendNotification(notification);
    }


    public void showExpandedWearNotification(View b) {

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(mainbg);

        final NotificationCompat.Builder builder = createNotificationBuilder();
        builder.extend(wearableExtender);
        sendNotification(builder.build());

    }

    public void showExpandedWearPagesNotification(View b) {
        Notification curryNoodlesPage = createCurryNoodlesPage();
        Notification koayteowPage = createKoayTeowPage();
        Notification oysterPage = createOysterOmelettePage();

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(mainbg)
                        .addPage(curryNoodlesPage)
                        .addPage(koayteowPage)
                        .addPage(oysterPage);

        PendingIntent pendingIntent = createIntent();

        final NotificationCompat.Builder builder = createNotificationBuilder();
        builder.addAction(android.R.drawable.ic_menu_view, "Curry", pendingIntent);
        builder.addAction(android.R.drawable.ic_menu_view, "Koay Teow", pendingIntent);
        builder.addAction(android.R.drawable.ic_menu_view, "Oyster", pendingIntent);

        builder.extend(wearableExtender);
        sendNotification(builder.build());


    }

    private Notification createCurryNoodlesPage() {
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle("Curry Noodles");

        return new NotificationCompat.Builder(this)
                .setStyle(style)
                .setContentText("Mouth Watering!")
                .extend(new NotificationCompat.WearableExtender()
                        .setBackground(curryNoodles)
                        .setContentAction(0))

                .build();
    }

    private Notification createKoayTeowPage() {
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle("Koay Teow")
                .bigText("Looks Yummy!");

        return new NotificationCompat.Builder(this)
                .setStyle(style)
                .extend(new NotificationCompat.WearableExtender()
                        .setBackground(koayTeow)
                        .setContentAction(1))

                .build();
    }

    private Notification createOysterOmelettePage() {
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle("Oyster Omelette")
                .bigText("This makes me really hungry!");

        return new NotificationCompat.Builder(this)
                .setStyle(style)
                .extend(new NotificationCompat.WearableExtender()
                        .setBackground(oyster)
                        .setContentAction(2))
                .build();
    }

    private NotificationCompat.Builder createNotificationBuilder() {
        PendingIntent pendingIntent = createIntent();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Message from Weng")
                .setContentText("Checkout these must-try dishes")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        return builder;
    }


    private PendingIntent createIntent() {
        Intent intent = new Intent(this, ContentActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    private void sendNotification(Notification notification) {
        final NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        notification.priority = Notification.PRIORITY_LOW;
        managerCompat.notify(0, notification);
    }
}
