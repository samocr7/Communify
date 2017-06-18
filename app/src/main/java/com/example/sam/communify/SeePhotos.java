package com.example.sam.communify;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.defaultValue;

public class SeePhotos extends AppCompatActivity {
    //ImageView photoView;
    ImageView image;
    LinearLayout mLayout;
    String desc;
    String date;
    File fullPhoto= null;
    File photoFile = null;
    String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_photos);
        Intent intent = getIntent();
        //String key = intent.getStringExtra("key");
        //String desc = intent.getStringExtra("desc");
        //Log.d("key", key);
        ArrayList<String> imgKeys = intent.getStringArrayListExtra("imgKey");
        ArrayList<String> descriptions = intent.getStringArrayListExtra("desc");
        ArrayList<String> dates = intent.getStringArrayListExtra("date");
        //Toast.makeText(SeePhotos.this, ""+latitude+" "+longitude, Toast.LENGTH_SHORT).show();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mLayout = (LinearLayout)findViewById(R.id.layout);

        for (int i=0; i < imgKeys.size(); i++) {
             String key = imgKeys.get(i);
             desc = descriptions.get(i);
            date = dates.get(i);
            StorageReference imgRef = mStorageRef.child("images/" + key);

            //EditText imgDesc = (EditText) findViewById(R.id.imgDesc);
            //imgDesc.setKeyListener(null);
            //imgDesc.setText(desc);

            final long ONE_MEGABYTE = 5024 * 5024;
            //Toast.makeText(SeePhotos.this, "MADE IT HERE", Toast.LENGTH_SHORT).show();


            imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    //Toast.makeText(SeePhotos.this, "MADE IT HERE!!!", Toast.LENGTH_SHORT).show();
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap thumbnail = Bitmap.createScaledBitmap(bmp, 250, 250, false);
                    //Log.d("imageAdded", key);
                    setImage(thumbnail, bmp, desc, date);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //error
                }
            });
        }
    }

    private File createImageFile(){
        String imageFileName = "12345";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            fullPhoto = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCurrentPhotoPath=fullPhoto.getAbsolutePath();
        return fullPhoto;
    }

    private void setImage(Bitmap thumbnail, Bitmap bmp, final String desc, final String date){
        image = new ImageView(this);
        Bitmap fullImage = bmp;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        final byte[] bytes = stream.toByteArray();
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins(0,20,20,20);
        image.setLayoutParams(param);
        image.setImageBitmap(thumbnail);
        mLayout.addView(image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFullPhoto = new Intent(v.getContext(), FullPhoto.class);
                String imgDesc = desc;
                String imgDate = date;
                fullPhoto = createImageFile();
                try {
                    FileOutputStream fs = new FileOutputStream(fullPhoto);
                    fs.write(bytes);
                }catch(Exception e) {
                    e.printStackTrace();
                }
                goToFullPhoto.putExtra("photoPath", mCurrentPhotoPath);
                //goToFullPhoto.putExtra("image", bytes);
                goToFullPhoto.putExtra("desc", imgDesc);
                goToFullPhoto.putExtra("date", imgDate);
                startActivity(goToFullPhoto);
            }
        });

    }
}

