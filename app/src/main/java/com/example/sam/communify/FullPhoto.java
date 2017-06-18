package com.example.sam.communify;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

public class FullPhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_photo);

        Intent intent = getIntent();
        String directory = intent.getStringExtra("photoPath");
        File path = new File(directory);
        Bitmap photo = BitmapFactory.decodeFile(directory);
        String desc = intent.getStringExtra("desc");
        String date = intent.getStringExtra("date");
        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setImageBitmap(photo);
        EditText mDate = (EditText) findViewById(R.id.date);
        mDate.setText(date);
        mDate.setKeyListener(null);
        EditText mDesc = (EditText) findViewById(R.id.desc);
        mDesc.setKeyListener(null);
        mDesc.setText(desc);

    }
}
