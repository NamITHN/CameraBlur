package com.example.sev_user.myappt20;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.sev_user.myappt20.helper.BitmapUtil;
import com.example.sev_user.myappt20.helper.BlurImage;
import com.example.sev_user.myappt20.helper.Constants;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TAG";
    private static final int PERMISSION_CAMERA = 1;
    private static final int CAMERA_PIC_REQUEST = 2;
    Button btnLoad, btnProcess, btnCam;
    ImageView imgData;
    private static final int SELECT_GALLERY_IMAGE = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        btnLoad = findViewById(R.id.btn_load);
        btnProcess = findViewById(R.id.btn_process);
        imgData = findViewById(R.id.img_data);
        btnCam = findViewById(R.id.btn_cam);

        btnLoad.setOnClickListener(this);
        btnProcess.setOnClickListener(this);
        btnCam.setOnClickListener(this);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CAMERA);
            } else {
                Log.d(TAG, "checkPermission: vao 1");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load:
                loadImage();
                break;
            case R.id.btn_process:
                processImage();
                break;
            case R.id.btn_cam:
                camera();
                break;
            default:
                break;
        }
    }

    private void camera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }

    private void processImage() {

    }

    private void loadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (data == null) return;
        if (requestCode == SELECT_GALLERY_IMAGE) {
            bitmap = BitmapUtil.getBitmapFromGallery(this, data.getData());
        }
        if(requestCode==CAMERA_PIC_REQUEST){
            bitmap = (Bitmap) data.getExtras().get("data");
        }
        if (bitmap != null)
        try {
            Bitmap segmented = BlurImage.getBluredImage(bitmap, getApplicationContext(), Constants.BLUR);
            imgData.setImageBitmap(segmented);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
