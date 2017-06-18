package com.example.sam.communify;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.tag;
import static android.R.id.message;

public class UploadPhoto extends AppCompatActivity {
    ImageView photoView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public double latitude;
    public double longitude;
    String directory;
    StorageReference mStorageRef;
    DatabaseReference getCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Button uploadPhoto = (Button) findViewById(R.id.uploadPhoto);
        photoView = (ImageView) findViewById(R.id.PhotoImageView);

        Intent intent = getIntent();
        directory = intent.getStringExtra("photoPath");
        File path = new File(directory);
        Bitmap photo = BitmapFactory.decodeFile(directory);
        photoView.setImageBitmap(photo);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude= location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);

                return;
            } else {

            }
        }
        locationManager.requestLocationUpdates("gps", 500, 0, locationListener);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case 10:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates("gps", 500, 0, locationListener);
                return;
        }
    }


    public void uploadPhoto(View view) throws IOException {
        EditText desc = (EditText) findViewById(R.id.editText);
        String imgDesc = desc.getText().toString();
        if (imgDesc != null || !imgDesc.equals("") || imgDesc.length() > 0) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference myRef = db.getReference("imageData");
            Calendar c = Calendar.getInstance();
            String date = "" + c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR);
            //ImageData data = new ImageData(latitude, longitude, time, "temp");
            String key = myRef.push().getKey();
            myRef.child(key).setValue(new ImageData(latitude, longitude, date, imgDesc));
            Uri file = Uri.fromFile(new File(directory));
            StorageReference ref = mStorageRef.child("images/" + key);
            ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AlertDialog alertDialog = new AlertDialog.Builder(UploadPhoto.this).create();
                            alertDialog.setTitle("ERROR");
                            alertDialog.setMessage("ERROR!");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "FUK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.show();
                        }
                    });
            Toast.makeText(UploadPhoto.this, "Thanks for uploading a photo! Check it out on the map!", Toast.LENGTH_LONG).show();
            Intent menu = new Intent(this, MainActivity.class);
            startActivity(menu);




        }else{
            Toast.makeText(UploadPhoto.this, "Please describe your photo!", Toast.LENGTH_SHORT).show();
        }

    }


}


