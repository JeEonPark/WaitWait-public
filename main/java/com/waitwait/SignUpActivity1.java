package com.waitwait;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SignUpActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);
    }

    public void onClickSignUpButtonClicked(View view){
        Intent intent = new Intent(getApplicationContext(), SignUpActivity2.class);
        startActivity(intent);
    }
}
