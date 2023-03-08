package com.android.sample.mainproj.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.android.sample.mainproj.R;
import com.android.sample.mainproj.dialog.PermissionDialog;

import com.android.sample.mainproj.log.LogService;
import com.android.sample.mainproj.service.LocationService;
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

public class MapActivity extends AppCompatActivity {

    private final int REQUEST_MAP_ACCESS = 1005;

    private Activity activity;

    private ImageButton ibtn_back_click;

    private TextView tv_latitude, tv_longitude;

    private Button btn_detect_pos, btn_back_detect_pos;

    private PermissionDialog dialog = null;

    private LocationRequest locationRequest;

    private Boolean locDetectStatus = false;

    private Boolean locBackDetectStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.activity_map);

            init();

            setting();

            addListener();

        }
        catch (Exception ex) {

            LogService.error(this, ex.getMessage(), ex);
        }
    }

    private void init() {

        activity = this;

        ibtn_back_click = findViewById(R.id.ibtn_back_click);

        tv_latitude = findViewById(R.id.tv_latitude);

        tv_longitude = findViewById(R.id.tv_longitude);

        btn_detect_pos = findViewById(R.id.btn_detect_pos);

        btn_back_detect_pos = findViewById(R.id.btn_back_detect_pos);

    }

    private void setting() {

        locationRequest = LocationRequest.create();
    }

    private void addListener() {

        ibtn_back_click.setOnClickListener(listener_back_click);

        btn_detect_pos.setOnClickListener(listener_detect_pos);

        btn_back_detect_pos.setOnClickListener(listener_back_detect_pos);
    }

    private View.OnClickListener listener_back_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            finish();
        }
    };

    private View.OnClickListener listener_detect_pos = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(checkMapPermission()) {

                locDetectStatus = !locDetectStatus;

                if(locDetectStatus) {
                    //위치 감지 시작 기능 구현
                    locationRequest.setInterval(1000);
                    locationRequest.setFastestInterval(1000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

                    btn_detect_pos.setText("위치 감지 중지");

                    Toast.makeText(activity, "위치 감지를 시작합니다.", Toast.LENGTH_SHORT).show();

                }
                else {
                    //위치 감지 중지 기능 구현
                    LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(locationCallback);

                    btn_detect_pos.setText("위치 감지 시작");

                    Toast.makeText(activity, "위치 감지를 중지합니다.", Toast.LENGTH_SHORT).show();

                }

            }
        }
    };


    private View.OnClickListener listener_back_detect_pos = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // ACCESS_BACKGROUND_LOCATION : 백그라운드에서 위치를 조회할 수 있는 권한
            //해당 권한이 경우 항상 명시적 권한을 거부한 상태로 처리하기 때문에
            //사용자가 직저 설정에서 변경해주어야 하며 항상 허용 설정으로 설정하여야 한다.
            //shouldShowRequestPermissionRationale : 사용자가 명시적으로 거부한 경우 true가 발생
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == false) {

                Intent locIntent = null;

                if(LocationService.intent == null) {

                    locIntent = new Intent(LocationService.ACTION_NAME);
                    locIntent.setPackage(getPackageName());
                }
                else {

                    locIntent = LocationService.intent;
                }

                locBackDetectStatus = !locBackDetectStatus;

                if(locBackDetectStatus) {

                    startService(locIntent);

                    btn_back_detect_pos.setText("백그라운드 위치 감지 중지");
                }
                else {

                    stopService(locIntent);

                    btn_back_detect_pos.setText("백그라운드 위치 감지 시작");
                }
            }
            else {

                requestCustomPermission();
            }


        }
    };

    private Boolean checkMapPermission() {

        //ACCESS_FINE_LOCATION : 기기의 우치 추정치 데이터 접근 권한 요청
        //ACCESS_COARSE_LOCATION : 최대한 정확한 기기의 위치 추정치 데이터 접근 권한 요청
        if(
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            String[] permissions = {

                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

            requestPermissions(permissions, REQUEST_MAP_ACCESS);
        }
        else {

            return true;  //권한이 있으면 TRUE
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 ) {

            if(requestCode == REQUEST_MAP_ACCESS) {

                if(
                        (
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                        ) == false
                ) {

                    requestUserPermission("위치 접근");
                }
            }
        }
    }

    private void requestUserPermission(String perm) {

        dialog = new PermissionDialog(activity, perm);

        dialog.setDialogClickListener(new PermissionDialog.OnDialogClickListener() {
            @Override
            public void onYesClick() {

                Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

                appDetail.addCategory(Intent.CATEGORY_DEFAULT);

                appDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(appDetail);
            }

            @Override
            public void onNoClick() {

                finish();
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {

            super.onLocationResult(locationResult);

            if(locationResult != null && locationResult.getLastLocation() != null) {

                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();

                tv_latitude.setText(String.valueOf(latitude));
                tv_longitude.setText(String.valueOf(longitude));

                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gv_map);

                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {

                        LatLng myPosition = new LatLng(latitude, longitude);

                        googleMap.clear();

                        googleMap.addMarker(new MarkerOptions().position(myPosition).title("나의 위치"));

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 16));
                    }
                });
            }
        }
    };

    private void  requestCustomPermission() {

        dialog = new PermissionDialog(activity, "");
        dialog.setDialogClickListener(new PermissionDialog.OnDialogClickListener() {
            @Override
            public void onYesClick() {

                Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

                appDetail.addCategory(Intent.CATEGORY_DEFAULT);

                appDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(appDetail);
            }

            @Override
            public void onNoClick() {

                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        dialog.setDialogText("백그라운드 위치 확인을 위해\n사용자가 직접 위치에 대한 권한을\n항상 허용으로 설정할 필요가 있습니다.\n변경화면으로 이동하시겠습니까?");

        dialog.show();
    }
}