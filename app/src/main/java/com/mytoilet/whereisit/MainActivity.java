package com.mytoilet.whereisit;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;

import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.mytoilet.R;
import com.facebook.stetho.Stetho;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TMapGpsManager.onLocationChangedCallback {
    private Toolbar myToolbar;
    private TextView mTitle;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private TMapGpsManager myMarker = null;
    private TMapView tmapview;
    private TMapData tmapData;
    private ListView listView;
    private ListViewAdapter adapter;
    private Uri mImageCaptureUri;
    private AddRequestDialog request_dlg;
    private File file;
    private static final int PICK_FROM_CAMERA = 0;

    private static final int PICK_FROM_ALBUM = 1;







    private boolean isPerGranted = false;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);


        adapter = new ListViewAdapter();
        listView = (ListView)findViewById(R.id.toilet_list);
        listView.setAdapter(adapter);

        tmapData = new TMapData();
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey(getString(R.string.tmap_app_key));
        addClickListener();
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
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Button button = findViewById(R.id.switchListButton);
            switch (item.getItemId()) {
                case R.id.action_around:

                    addTMapMarkerItem();
                    button.setEnabled(true);
                    break;
                case R.id.action_regional:
                    if(myMarker!=null)
                        myMarker.CloseGps();
                    RegionalDialog region_dlg = new RegionalDialog(MainActivity.this, tmapview,mRealm,adapter);
                    region_dlg.openDialog();
                    button.setEnabled(true);
                    break;
                case R.id.action_add:
                    request_dlg = new AddRequestDialog(MainActivity.this);
                    request_dlg.openDialog();
                    break;

            }
            return true;
        });


        // 3. Tmap 생성
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.tmap_layout);
        layout.addView(tmapview);

        // 4. Navigation Drawer 생성 및 초기화
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;


        switch(requestCode)
        {
            case PICK_FROM_ALBUM:
            {
                mImageCaptureUri = data.getData();
                ImageView imageView1 = (ImageView)request_dlg.getDialog().findViewById(R.id.imageUpload1);
                imageView1.setImageURI(mImageCaptureUri);
                break;
            }


            case PICK_FROM_CAMERA:
            {
                if(data.hasExtra("data"))
                {
                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                    ImageView imageView = (ImageView)request_dlg.getDialog().findViewById(R.id.imageUpload1);
                    imageView.setImageBitmap(bitmap);
                }

                break;
            }


        }


    }


    private void addTMapMarkerItem() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.custom_marker);

        if (myMarker == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("현재 위치를 조회 해 주시기 바랍니다.");
            builder.setPositiveButton("예",
                    (dialog, which) -> Toast.makeText(getApplicationContext(), "먼저 현재 위치를 조회해주세요.", Toast.LENGTH_LONG).show());
            builder.show();
        } else {
            Thread th = new Thread(() -> {
                int count = 0;

                mRealm = Realm.getDefaultInstance();
                RealmQuery<Toilet> query = mRealm.where(Toilet.class);

                RealmResults<Toilet> toiletList = query.findAll();
                toiletList = filterMarker(toiletList);


                adapter.clear();
                adapter.notifyDataSetChanged();
                tmapview.removeAllMarkerItem();
                tmapview.removeAllTMapCircle();
                tmapview.removeAllTMapPolygon();
                tmapview.removeAllTMapPOIItem();
                int size = toiletList.size();
                for (int i = 0; i < size; i++) {
                    TMapMarkerItem item = new TMapMarkerItem();
                    if (toiletList.get(i).lat == 0 || toiletList.get(i).lon == 0)
                        continue;
                    else {
                        item.setTMapPoint(new TMapPoint(toiletList.get(i).lat, toiletList.get(i).lon));
                    }
                    double distance = getDistance(myMarker.getLocation().getLatitude(), myMarker.getLocation().getLongitude(), item.latitude, item.longitude);
                    if (distance < 2) {
                        item.setIcon(icon);
                        item.setVisible(TMapMarkerItem.VISIBLE);
                        item.setName(toiletList.get(i).toilet_name);
                        adapter.addItem(item.getName(),toiletList.get(i).toilet_addr1.equals("")? toiletList.get(i).toilet_addr2:toiletList.get(i).toilet_addr1, distance);
                        tmapview.addMarkerItem(String.valueOf(toiletList.get(i).toilet_id), item);

                        count++;
                    }
                }
                TMapCircle circle = new TMapCircle();
                circle.setCenterPoint(myMarker.getLocation());
                circle.setAreaColor(Color.GRAY);
                circle.setAreaAlpha(100);
                circle.setRadius(2000);
                circle.setRadiusVisible(true);
                tmapview.addTMapCircle("범위",circle);

                adapter.sort();
                adapter.notifyDataSetChanged();

                int finalCount = count;
                runOnUiThread(() -> {
                    Toast toast = Toast.makeText(MainActivity.this, "검색 결과 " + finalCount + "개 찾았습니다.", Toast.LENGTH_LONG);
                    toast.show();
                });

                mRealm.close();

            });
            th.start();

        }

    }

    public RealmResults<Toilet> filterMarker(RealmResults<Toilet> toilets){
        CheckBox checkBox1 = (CheckBox)findViewById(R.id.checkbox_seperate);
        CheckBox checkBox2 = (CheckBox)findViewById(R.id.checkbox_both);
        RadioButton radioButton1 = (RadioButton)findViewById(R.id.radioButton_cancel);
        RadioButton radioButton2 = (RadioButton)findViewById(R.id.radioButton_handicapped);
        RadioButton radioButton3 = (RadioButton)findViewById(R.id.radioButton_child);
        RealmQuery<Toilet> result = toilets.where();
        if(checkBox1.isChecked() && checkBox2.isChecked())
        {
            result.equalTo("isToiletBoth",false).or().equalTo("isToiletBoth",true);
        }
        else if(checkBox1.isChecked())
        {
            result.equalTo("isToiletBoth",false);
        }
        else if(checkBox2.isChecked())
        {
            result.equalTo("isToiletBoth",true);
        }

        if(radioButton1.isChecked())
        {
            //Do nothing
        }
        else if(radioButton2.isChecked()) // 장애인용 쿼리
        {

            for(int i=0;i<toilets.size();i++)
            {
                Toilet toilet = toilets.get(i);

                if(toilet.toilets_count.get(2) != 0 || toilet.toilets_count.get(3) !=0 || toilet.toilets_count.get(7)!=0) {

                    result.or().equalTo("toilet_id", toilet.toilet_id);
                }
            }//2,3,7번째

        }
        else if(radioButton3.isChecked()) // 어린이용 쿼리
        {
            //4,5,8번째 index 값 0인지 아닌지 확인(or)
            for(int i=0;i<toilets.size();i++)
            {
                Toilet toilet = toilets.get(i);

                if(toilet.toilets_count.get(4) != 0 || toilet.toilets_count.get(5) !=0 || toilet.toilets_count.get(8)!=0)
                {
                    result.or().equalTo("toilet_id",toilet.toilet_id);
                }
            }
        }
        toilets = result.findAll();
        return toilets;
    }

    private void addClickListener() {
        tmapview.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                mRealm = Realm.getDefaultInstance();
                for(TMapMarkerItem markerItem: arrayList)
                {
                    RealmQuery<Toilet> query = mRealm.where(Toilet.class).equalTo("toilet_id",Integer.parseInt(markerItem.getID()));
                    Toilet toilet = query.findFirst();
                    BalloonDialog dialog = new BalloonDialog(MainActivity.this,toilet,tmapview, mRealm);
                    dialog.openDialog();
                }

                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }
        });
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

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6373.0; //meters
        double dLat = Math.toRadians(lat2) - Math.toRadians(lat1);
        double dLng = Math.toRadians(lon2) - Math.toRadians(lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist; //km로 환산
    }

    public void onImageClick(View v) {
        if (v.getId() == R.id.imageUpload1) {
            DialogInterface.OnClickListener cameraListener = (dialog, which) -> {
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1001);
                } else {
                    try {
                        doTakePhotoAction();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            DialogInterface.OnClickListener albumListener = (dialog, which) -> {
                doTakeAlbumAction();
            };
            DialogInterface.OnClickListener cancelListener = (mDialog, which) -> mDialog.dismiss();
            new AlertDialog.Builder(this)
                    .setTitle("업로드할 이미지 선택")
                    .setNeutralButton("앨범에서 가져오기", albumListener)
                    .setPositiveButton("닫기", cancelListener)
                    .setNegativeButton("사진촬영", cameraListener)
                    .show();
        }
    }

    private void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);


        startActivityForResult(intent, PICK_FROM_ALBUM);




    }

    private void doTakePhotoAction() throws IOException {


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        startActivityForResult(intent, PICK_FROM_CAMERA);

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


                myMarker = new TMapGpsManager(MainActivity.this);
                myMarker.setMinTime(1000);
                myMarker.setMinDistance(5);
                myMarker.setProvider(myMarker.NETWORK_PROVIDER);
                myMarker.OpenGps();

                tmapview.setIconVisibility(true);
                tmapview.setTrackingMode(true);
                tmapview.setSightVisible(true);
                tmapview.setCenterPoint(longitude, latitude, true);
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
        if (isPerGranted) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }


    public void onStop() {
        super.onStop();
        if (myMarker != null) {
            myMarker.CloseGps();
        }
    }

    public void onSwitchButtonClick(View v)
    {
        Button b1 = (Button)v;
        ViewSwitcher switcher = (ViewSwitcher)findViewById(R.id.switch_layout);
        if(b1.getText().equals("리스트로 전환"))
        {
            b1.setOnClickListener(v1 -> {
                switcher.showNext();
            });
        }
        else
        {
            b1.setOnClickListener(v12 -> {
                switcher.showPrevious();
            });
        }

    }


}





