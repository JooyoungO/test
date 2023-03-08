package com.android.sample.mainproj.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.sample.mainproj.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationService extends Service {

    public static Intent intent;

   private static LocationRequest locationRequest = null;

   public static final String ACTION_NAME = "com.android.service.LOCATION";

   private final int LOCATION_SERVICE_ID = 101;

   private NotificationManager notificationManager;

   private NotificationCompat.Builder builder;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        if(locationRequest == null) {

            locationRequest = LocationRequest.create();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.intent = intent;

        if(locationRequest != null) {
            //기능 실행
            startLocationBackService();
        }

        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void startLocationBackService() {

        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        builder = getDefaultBuilder();

        startForeground(LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationBackService() {

        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        stopLocationBackService();
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult)
        {
            super.onLocationResult(locationResult);

            if (locationResult != null && locationResult.getLastLocation() != null)
            {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLatitude();

                if(notificationManager != null && builder != null)
                {
                    builder.setContentText("위도 : " + latitude + " 경도 : " + longitude);
                    notificationManager.notify(LOCATION_SERVICE_ID, builder.build());
                }

                Log.i(this.getClass().getName(), "위도 : " + latitude + " 경도 : " + longitude);
            }
        }
    };

    private NotificationCompat.Builder getDefaultBuilder()
    {
        String channelID = "loc_notification_channel";

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // API 26 버전 이상부터 알림 통지를 위해서 알림을 받을 수 있는 채널을 생성하여야 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelID) == null)
            {
                // IMPORTANCE_HIGH : 알림의 중요도 설정
                NotificationChannel notificationChannel = new NotificationChannel
                        (
                                channelID,
                                "location Notification Channel",
                                NotificationManager.IMPORTANCE_NONE
                        );

                notificationChannel.setDescription("지도 알림 채널");
                notificationChannel.setSound(null, null);
                notificationChannel.setShowBadge(false);
                notificationChannel.setVibrationPattern(new long[]{ 0 });
                notificationChannel.enableVibration(true);

                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        Intent resultIntent = new Intent();

        // FLAG_UPDATE_CURRENT : 이미 존재할경우 해당 인텐트로 대체
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),1, resultIntent,PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);

        builder.setSmallIcon(R.mipmap.ic_launcher);

        builder.setContentTitle("맵");

        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        builder.setVibrate(new long[] { -1 });

        builder.setOnlyAlertOnce(true);

        builder.setContentText("맵 정보 호출중입니다.");

        builder.setContentIntent(pendingIntent);

        builder.setAutoCancel(false);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder;
    }
}