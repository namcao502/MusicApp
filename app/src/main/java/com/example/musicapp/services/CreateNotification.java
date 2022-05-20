package com.example.musicapp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicapp.R;
import com.example.musicapp.models.ArtistModel;
import com.example.musicapp.models.SongModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CreateNotification {
    public static final String CHANNEL_ID = "channel";
    public static final String ACTION_PREVIOUS = "actionPrevious";
    public static final String ACTION_PLAY = "actionPlay";
    public static final String ACTION_NEXT = "actionNext";

    public static Notification notification;

    public static void CreateNotification(Context context, SongModel songModel, int playAndPauseButton, int position, int size, Bitmap bitmap){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            PendingIntent pendingIntentPrevious;
            int drw_previous;
            if (position == 0){
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else {
                Intent intentPrevious = new Intent(context, NotificationActionService.class).setAction(ACTION_PREVIOUS);
                pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
                drw_previous = R.drawable.ic_skip_previous_black_24dp;
            }

            Intent intentPlay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntentNext;
            int drw_next;
            if (position == size){
                pendingIntentNext = null;
                drw_next = 0;
            } else {
                Intent intentNext = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
                drw_next = R.drawable.ic_skip_next_black_24dp;
            }

            int artistListLength = songModel.getArtist().size();

            final String[] artistText = {""};
            artistText[0] = "";

            for (int i=0; i<artistListLength; i++){
                FirebaseFirestore.getInstance().collection("Artist").document(songModel.getArtist().get(i))
                        .get().addOnCompleteListener(task -> {
                    DocumentSnapshot doc = task.getResult();
                    ArtistModel artistModel = doc.toObject(ArtistModel.class);
                    artistText[0] += artistModel.getName() + ", ";
//                    Bitmap bitmap = getBitmapFromURL(imgUrl);

                    notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.icons8_musical_notes_96)
                            .setContentTitle(songModel.getTitle())
                            .setContentText(artistText[0])//
                            .setLargeIcon(bitmap)
                            .setOnlyAlertOnce(true)//show notification for only first time
                            .setShowWhen(false)
                            .addAction(drw_previous, "Previous", pendingIntentPrevious)
                            .addAction(playAndPauseButton, "Play", pendingIntentPlay)
                            .addAction(drw_next, "Next", pendingIntentNext)
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                    .setShowActionsInCompactView(0, 1, 2)
                                    .setMediaSession(mediaSessionCompat.getSessionToken()))
                            .setPriority(NotificationCompat.PRIORITY_LOW)
                            .build();
                    notificationManagerCompat.notify(1, notification);
                });
            }


        }
    }
}
