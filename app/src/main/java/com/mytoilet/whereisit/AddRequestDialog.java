package com.mytoilet.whereisit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mytoilet.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddRequestDialog implements com.mytoilet.whereisit.Dialog{
    private Context context;
    private Dialog dialog;

    public AddRequestDialog(Context context) {
        this.context = context;
    }

    public void openDialog() {
        final Dialog dlg = new Dialog(context);
        dialog = dlg;
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.addrequest_dialog);
        dlg.show();
        final TextInputLayout text = (TextInputLayout) dlg.findViewById(R.id.text_layout);
        Button okButton = (Button) dlg.findViewById(R.id.request_confirm);
        Button cancelButton = (Button) dlg.findViewById(R.id.request_cancel);
        Button currentLocation = (Button) dlg.findViewById(R.id.getCurrent);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                try {
                    emailIntent.setType("image/*");
                    emailIntent.setPackage("com.google.android.gm");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.email_address)});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "화장실 추가 요청");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, text.getEditText().getText().toString());


                    ImageView imageView = (ImageView) dlg.findViewById(R.id.imageUpload1);
                    Bitmap bitmap = imageView.getDrawingCache();
                    File myDir = new File(Environment.getExternalStorageDirectory() + "/");
                    myDir.mkdirs();
                    DateFormat format = new SimpleDateFormat("yyyy_MM_dd_H_mm_ss", Locale.getDefault());
                    Date curDate = new Date();
                    String displayDate = format.format(curDate);
                    String fname = displayDate + "_Image-Report.png"; // now this is dynamic
                    // create the file in the directory
                    File file = new File(myDir, fname);
                    try
                    {
                        boolean fileCreated = file.createNewFile();
                        if(fileCreated)
                        {
                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    emailIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getPackageName(), file));
                    context.startActivity(Intent.createChooser(emailIntent, "Send Mail Choosing : "));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dlg.dismiss();

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker gpsTracker = new GpsTracker(context);
                double longitude = gpsTracker.getLongitude();
                double latitude = gpsTracker.getLatitude();

                String address = getCurrentAddress(latitude, longitude);
                text.getEditText().setText(address);
            }

            private String getCurrentAddress(double latitude, double longitude) {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                List<Address> addresses;

                try {

                    addresses = geocoder.getFromLocation(
                            latitude,
                            longitude,
                            7);
                } catch (IOException ioException) {
                    //네트워크 문제
                    Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
                    return "지오코더 서비스 사용불가";
                } catch (IllegalArgumentException illegalArgumentException) {
                    Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
                    return "잘못된 GPS 좌표";

                }



                if (addresses == null || addresses.size() == 0) {
                    Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show();
                    return "주소 미발견";

                }

                Address address = addresses.get(0);
                return address.getAddressLine(0);

            }
        });
    }


    public Dialog getDialog()
    {
        return dialog;
    }

}
