package com.mytoilet.whereisit;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytoilet.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;


import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar myToolbar;
    TextView mTitle;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    TMapMarkerItem markerItem;
    TMapView tmapview;
    LocationNavigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = new LocationNavigator();

        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey(getString(R.string.tmap_app_key));
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 1. Toolbar 생성
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextAppearance(this, R.style.AppTitleTextAppearance);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setText(myToolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_appbar_menu);

        // 2. BottomAppBar 생성
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_around:
                        if (markerItem != null) {
                            ToiletFinder finder = new ToiletFinder();
                            finder.findToiletByNear(markerItem);
                        }
                        break;
                    case R.id.action_regional:
                        RegionalDialog region_dlg = new RegionalDialog(MainActivity.this);
                        region_dlg.openDialog(markerItem);
                        break;
                    case R.id.action_add:
                        AddRequestDialog request_dlg = new AddRequestDialog(MainActivity.this);
                        request_dlg.openDialog();
                        break;

                }
                return true;
            }
        });


        // 3. Tmap 생성
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.tmap_layout);
        layout.addView(tmapview);

        // 4. Navigation Drawer 생성
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }


    public void onImageClick(View v)
    {
        if(v.getId()==R.id.imageUpload1||v.getId()==R.id.imageUpload2)
        {
            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                    if(permissionCheck==PackageManager.PERMISSION_DENIED)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},1001);
                    }
                    else
                    {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, ResultCode.PICK_FROM_CAMERA.ordinal());
                    }
                }
            };
            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent,ResultCode.PICK_FROM_ALBUM.ordinal());
                }
            };
            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface mDialog, int which) {
                    mDialog.dismiss();
                }
            };
            new AlertDialog.Builder(this)
                    .setTitle("업로드할 이미지 선택")
                    .setNeutralButton("앨범에서 가져오기", albumListener)
                    .setPositiveButton("닫기",cancelListener)
                    .setNegativeButton("사진촬영",cameraListener)
                    .show();
        }
    }


    public void onFabClick(View v) {

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //권한 승인 시
                final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                markerItem = MyMarker.getInstance();
                TMapMarkerItem item2 = new TMapMarkerItem();
                TMapPoint tMapPoint = new TMapPoint(latitude,longitude);

                // 마커 아이콘
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_my_marker);


                item2.setIcon(bitmap);
                item2.setPosition(0.5f, 1.0f);
                item2.setTMapPoint(new TMapPoint(36.143532, 128.392676));
                item2.setName("목적지_임시");

                markerItem.setIcon(bitmap); // 마커 아이콘 지정
                markerItem.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
                markerItem.setTMapPoint( tMapPoint ); // 마커의 좌표 지정
                markerItem.setName("현재위치"); // 마커의 타이틀 지정
                markerItem.setCanShowCallout(true);
                tmapview.addMarkerItem("현재위치", markerItem); // 지도에 마커 추가
                tmapview.addMarkerItem("목적지", item2);
                TMapCircle circle = new TMapCircle();
                circle.setCenterPoint(tMapPoint);
                circle.setRadius(1000);
                circle.setCircleWidth(2);
                circle.setLineColor(Color.BLUE);
                circle.setAreaColor(Color.GRAY);
                circle.setAreaAlpha(100);
                tmapview.addTMapCircle("circle1", circle);
                navigator.findPath(tmapview, markerItem, item2);
                tmapview.setCenterPoint(longitude,latitude,true);

                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        1000,
                        0,
                        locationListener);

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "위치를 설정할 수 없습니다. 위치권한을 허용해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("현재 위치를 찾을려면 위치 권한을 승인해줘야 합니다.")
                .setGotoSettingButton(true)
                .setDeniedMessage("위치를 설정할 수 없습니다. [설정] > [권한] 에서 위치 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
        }
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                TMapPoint mapPoint = new TMapPoint(latitude,longitude);
                markerItem.setTMapPoint(mapPoint);
            }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
        };

}





