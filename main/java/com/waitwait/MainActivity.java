package com.waitwait;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    // FrameLayout에 각 메뉴의 Fragment를 바꿔 줌
    protected FragmentManager fragmentManager = getSupportFragmentManager();
    // 4개의 메뉴에 들어갈 Fragment들
    private HomeFragment homeFragment = new HomeFragment();
    private SecondFragment secondFragment = new SecondFragment();
    //private Menu3Fragment menu3Fragment = new Menu3Fragment();

    FragmentTransaction transaction = fragmentManager.beginTransaction();

    TextView tv;
    private FirebaseFirestore db;

    static public Activity matvt;
    static public Context ctx;

    String callResName;

    TextView etv;



    Toolbar myToolbar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    SecondFragment sf = new SecondFragment();
                    sf.stopThread();
                    transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_dashboard:
                    HomeFragment hf = new HomeFragment();
                    hf.stopThread();
                    transaction.replace(R.id.frame_layout, secondFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        transaction = fragmentManager.beginTransaction();
        // 첫 화면 지정
        transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss();
        tv = findViewById(R.id.waitStatusTextView);
        db = FirebaseFirestore.getInstance();

        matvt = MainActivity.this;
        ctx = getApplicationContext();

        etv = findViewById(R.id.expectTextView);

        //첫번째 패널에 Gps와 식당 위치를 파악해서 거리 계산


        //두번째 패널에 음식 준비됐는지 확인하는 스레드
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        Thread.sleep(5000);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    //false 인지 알아오기
                    DocumentReference docRef = db.collection("UserInformation").document(LoginedUserInformation.email);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            System.out.println(document.getString("CallListCallNow"));
                            String isitcall = document.getString("CallListCallNow");
                            callResName = document.getString("CallListRestaurantName");
                            if(isitcall.equals("true")) {
                                System.out.println("if 안으로 들어옴");
                                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();

                                        db.collection("RestaurantList").document(callResName).collection("CallList").document(LoginedUserInformation.email).delete();


                                        //UserInformation의 대기 변경
                                        Map<String, Object> informationData = new HashMap<>();
                                        informationData.put("CallListRestaurantName", "none");
                                        informationData.put("CallListCallNow", "false");
                                        db.collection("UserInformation").document(LoginedUserInformation.email)
                                                .set(informationData, SetOptions.merge())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //Do nothing
                                                    }
                                                });
                                    }
                                });
                                alert.setMessage("음식이 준비되었어요!");
                                alert.show();
                            }
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;

    }


    public void onClickToQRActivityMain(View view){
        Intent intent = new Intent(getApplicationContext(), QRActivity.class);
        startActivity(intent);
    }

    public void onClickCancelBooked(View view){
        db.collection("RestaurantList").document(LoginedUserInformation.WaitingRestaurantCode).collection("WaitList").document(LoginedUserInformation.WaitingNumber + "").delete();

        Map<String, Object> user = new HashMap<>();
        user.put("WaitListRestaurantName", "none");
        user.put("WaitListRestaurantNumber", 0);
        user.put("WaitListRestaurantCode", "none");



        db.collection("UserInformation").document(LoginedUserInformation.email)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "예약이 취소되었습니다.", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        if (item.getItemId() != R.id.action_settings) {
            // User chose the "Settings" item, show the app settings UI..
            return false;
        }
        Toast.makeText(getApplicationContext(), "내 QR코드 정보 : "+LoginedUserInformation.email, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), MyQRActivity.class);
        startActivity(intent);

        return  true;

    }

    public void onClickToSearchActivity(View view){
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(intent);
    }



}

