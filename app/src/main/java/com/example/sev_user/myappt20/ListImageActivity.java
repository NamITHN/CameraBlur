package com.example.sev_user.myappt20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sev_user.myappt20.adapter.ImageBlurAdapter;
import com.example.sev_user.myappt20.helper.BitmapUtil;
import com.example.sev_user.myappt20.helper.Constants;

import java.util.ArrayList;
import java.util.List;

public class ListImageActivity extends AppCompatActivity implements ImageBlurAdapter.Iclick {
    RecyclerView rcList;
    List<String> image;
    ImageBlurAdapter adapter;
    TextView txtNoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_image);
        getSupportActionBar().setTitle("Danh sách ảnh xóa phông");
        rcList = this.findViewById(R.id.rl_image_blur);
        txtNoImage=this.findViewById(R.id.txt_no_image);

        image = new ArrayList<>();
        image = BitmapUtil.getAllList();
        if(image.size()>0){
            rcList.setVisibility(View.VISIBLE);
        }else {
            rcList.setVisibility(View.GONE);
        }
        adapter = new ImageBlurAdapter(image, this, this);
        rcList.setHasFixedSize(true);
        rcList.setLayoutManager(new GridLayoutManager(this, 3));
        rcList.setAdapter(adapter);
    }

    @Override
    public void itemClick(int pos) {
        Intent intent = new Intent(ListImageActivity.this, PictureActivity.class);
        intent.putExtra(Constants.CHECK,2);
        intent.putExtra(Constants.LINKIMAGE,image.get(pos));
        startActivity(intent);
    }
}
