package com.waitwait;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import static java.security.AccessController.getContext;

public class QRActivity extends AppCompatActivity {

    //view Objects
    private Button buttonScan;
    private TextView textViewName, textViewAddress, textViewResult;

    //qr code scanner object
    private IntentIntegrator qrScan;
    private LoginedUserInformation LUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        //View Objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewResult = (TextView)  findViewById(R.id.textViewResult);
        LUI = new LoginedUserInformation();

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //scan option
        qrScan.setPrompt("Scanning...");
        //qrScan.setOrientationLocked(false);
        qrScan.initiateScan();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(QRActivity.this, "취소!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                //qrcode 결과가 있으면
                Toast.makeText(QRActivity.this, "스캔완료!", Toast.LENGTH_SHORT).show();
                LUI.QRcodeCaptured = result.getContents();
                Intent intent = new Intent(getApplicationContext(), WaitConfirmActivity.class);
                startActivity(intent);
                finish();

//                try {
//                    //data를 json으로 변환
//                    JSONObject obj = new JSONObject(result.getContents());
//                    textViewName.setText(obj.getString("name"));
//                    textViewAddress.setText(obj.getString("address"));
//                    Intent intent = new Intent(getApplicationContext(), QRActivity.class);
//                    startActivity(intent);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    //Toast.makeText(MainActivity.this, result.getContents(), Toast.LENGTH_LONG).show();
//                    textViewResult.setText(result.getContents());
//                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
