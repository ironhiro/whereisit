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
import android.location.LocationManager;
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

import com.example.mytoilet.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TMapGpsManager.onLocationChangedCallback {
    private Toolbar myToolbar;
    private TextView mTitle;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private TMapGpsManager myMarker = null;
    private TMapView tmapview;
    private LocationNavigator navigator=null;
    private boolean isPerGranted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                        if (myMarker != null) {
                            ToiletFinder finder = new ToiletFinder();
                            finder.findToiletByNear(myMarker);
                        }
                        break;
                    case R.id.action_regional:
                        RegionalDialog region_dlg = new RegionalDialog(MainActivity.this);
                        region_dlg.openDialog();

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
                        isPerGranted = true;

                        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            return;
                        }
                        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();


                        TMapPoint tMapPoint = new TMapPoint(latitude,longitude);


                        myMarker = new TMapGpsManager(MainActivity.this);
                        myMarker.setMinTime(1000);
                        myMarker.setMinDistance(5);
                        myMarker.setProvider(myMarker.NETWORK_PROVIDER);
                        myMarker.OpenGps();

                        tmapview.setIconVisibility(true);
                        tmapview.setTrackingMode(true);
                        tmapview.setSightVisible(true);
                        TMapCircle circle = new TMapCircle();
                        circle.setCenterPoint(tMapPoint);
                        circle.setRadius(1000);
                        circle.setCircleWidth(2);
                        circle.setLineColor(Color.BLUE);
                        circle.setAreaColor(Color.GRAY);
                        circle.setAreaAlpha(100);

                        tmapview.addTMapCircle("circle1", circle);
                        tmapview.setCenterPoint(longitude,latitude,true);
                    }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

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


    @Override
    public void onLocationChange(Location location) {
        if(isPerGranted)
        {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }
}





