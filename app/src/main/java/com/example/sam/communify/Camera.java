package com.example.sam.communify;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class Camera extends AppCompatActivity {
static final int REQUEST_IMAGE_CAPTURE = 1;
    File image= null;
    File photoFile = null;
    ImageView photoView;
    String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button takePhoto = (Button) findViewById(R.id.takePhoto);
        photoView = (ImageView) findViewById(R.id.PhotoImageView);


        //Disable takePhoto button if the user does not have a camera
        if(!checkCamera()){
            takePhoto.setEnabled(false);
        }

    }

    private File createImageFile(){
        String imageFileName = "12345";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCurrentPhotoPath=image.getAbsolutePath();
        return image;
    }

    private boolean checkCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void launchCamera(View view) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = createImageFile();
        if(photoFile != null){
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        //Take a picture, and pass the image to OnActivityResult
        //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //get the photo
            //Bundle extras = data.getExtras();
            //Bitmap photo = (Bitmap) extras.get("data");

            Intent uploadPhoto = new Intent(this, UploadPhoto.class);
            uploadPhoto.putExtra("photoPath", mCurrentPhotoPath);
            startActivity(uploadPhoto);



        }

    }

}
