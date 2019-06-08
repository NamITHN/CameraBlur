package com.example.sev_user.myappt20;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sev_user.myappt20.helper.BlurImage;
import com.example.sev_user.myappt20.helper.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class PictureActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private ImageView imageView, imgView;
    private SeekBar sbBlur;
    private TextView txtDepth;
    private ProgressBar progressBar;
    private LinearLayout llP1, llP2;
    private static final String IMAGE_DIRECTORY = "/CustomImage";
    private Bitmap bitmap;
    private int check;
    Bitmap bitmapBlur = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        imgView = this.findViewById(R.id.img_view);
        imageView = findViewById(R.id.img);
        sbBlur = findViewById(R.id.sb_blur);
        llP1 = this.findViewById(R.id.ll_p1);
        llP2 = this.findViewById(R.id.ll_p2);
        txtDepth = this.findViewById(R.id.txt_depth);
        progressBar = this.findViewById(R.id.pr_process);

        sbBlur.setOnSeekBarChangeListener(this);
        bitmap = HomeActivity.bitmap;

        Intent intent = getIntent();
        check = intent.getIntExtra(Constants.CHECK, 0);
        if (check == 1) {
            llP1.setVisibility(View.VISIBLE);
            llP2.setVisibility(View.GONE);
            bitmap = showImage(0, bitmap);
            //saveImage(bitmapBlur);
        } else {
            llP1.setVisibility(View.GONE);
            llP2.setVisibility(View.VISIBLE);
            String link = intent.getStringExtra(Constants.LINKIMAGE);
            try {
                Bitmap bitmapTemp = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(Uri.fromFile(new File(link))));
                imgView.setImageBitmap(bitmapTemp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        //imageView.setImageBitmap(resizedBitmap);
        //  Toast.makeText(this, resizedBitmap.getWidth() + " ; " + resizedBitmap.getHeight(), Toast.LENGTH_SHORT).show();
    }

    private Bitmap showImage(final int blur, final Bitmap bitmap) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                 //   progressBar.setVisibility(View.VISIBLE);
                    bitmapBlur = BlurImage.getBluredImage(bitmap, PictureActivity.this, blur);
                    //Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapBlur, 2000, 2000, false);
                    imageView.setImageBitmap(bitmapBlur);
                  //  progressBar.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return bitmapBlur;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs());
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();   //give read write permission
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Toast.makeText(this, "Save Image success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ListImageActivity.class));
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, final int process, boolean b) {
        progressBar.setVisibility(View.VISIBLE);
        txtDepth.setText("Depth : " + process);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            bitmapBlur = BlurImage.getBluredImage(bitmap, PictureActivity.this, process);
                            //Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapBlur, 2000, 2000, false);
                            imageView.setImageBitmap(bitmapBlur);
                            progressBar.setVisibility(View.GONE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        },2000);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (check == 1) {
            getMenuInflater().inflate(R.menu.menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_save) {
            if (bitmapBlur != null) {
                saveImage(bitmapBlur);
            }

        }

        return super.onOptionsItemSelected(item);
    }
}