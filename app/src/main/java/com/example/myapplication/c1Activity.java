package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class c1Activity extends AppCompatActivity {

    Camera camera;
    Care care;
    MyPage myPage;

    // FrameLayout에 각 메뉴의 Fragment를 바꿔 줌
    //FragmentManager fragmentManager = getSupportFragmentManager();
    // 4개의 메뉴에 들어갈 Fragment들
//    private Camera Camera = new Camera();
//    private Care Care = new Care();
//    private MyPage MyPage = new MyPage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c1);

        camera = new Camera();
        care = new Care();
        myPage = new MyPage();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, camera).commit();

        NavigationBarView navigationBarView = findViewById(R.id.bottom);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_camera:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, camera).commit();
                        return true;
                    case R.id.nav_pic_paws:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, care).commit();
                        return true;
                    case R.id.nav_mypage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, myPage).commit();
                        return true;
                }
                return false;
            }
        });
//        ScrollView guidescroll = findViewById(R.id.guidescroll);
//        ImageButton downbtn = findViewById(R.id.downbtn);
//
//        downbtn.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(guidescroll.getVisibility() == View.VISIBLE)
//                    guidescroll.setVisibility(View.INVISIBLE);
//                else
//                    guidescroll.setVisibility(View.VISIBLE);
//            }
//        }) ;

//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
//        // 첫 화면 지정
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.frame_layout, Camera).commitAllowingStateLoss();

        // bottomNavigationView의 아이템이 선택될 때 호출될 리스너 등록
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                switch (item.getItemId()) {
//                    case R.id.nav_camera: {
//                        transaction.replace(R.id.frame_layout, Camera).commitAllowingStateLoss();
//                        break;
//                    }
//                    case R.id.nav_pic_paws: {
//                        transaction.replace(R.id.frame_layout, Care).commitAllowingStateLoss();
//                        break;
//                    }
//                    case R.id.nav_mypage: {
//                        transaction.replace(R.id.frame_layout, MyPage).commitAllowingStateLoss();
//                        break;
//                    }
//                }
//                return true;
//            }
//        });
    }
    public void onClick(View view){
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
    }
}