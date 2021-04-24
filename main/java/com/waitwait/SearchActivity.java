package com.waitwait;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(2000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_search);


    }

    public void onClickOut(View view){
        LoginedUserInformation.QRcodeCaptured = "OutbackSuwon";
        Intent intent = new Intent(getApplicationContext(),  WaitConfirmActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickSul(View view){
        LoginedUserInformation.QRcodeCaptured = "SoolTongBabTong";
        Intent intent = new Intent(getApplicationContext(),  WaitConfirmActivity.class);
        startActivity(intent);
        finish();
    }

}
