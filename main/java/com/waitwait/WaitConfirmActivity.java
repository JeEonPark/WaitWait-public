package com.waitwait;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WaitConfirmActivity extends AppCompatActivity {


    private LoginedUserInformation LUI;
    private TextView rn;
    private TextView wn;
    private String QRdata;
    private FirebaseFirestore db;
    private AlertDialog.Builder alert;
    private int count = 0;
    int myNumberDouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_confirm);

        rn = findViewById(R.id.RestaurantName);
        wn = findViewById(R.id.WaitingNumbers);
        LUI = new LoginedUserInformation();
        alert = new AlertDialog.Builder(this);

        QRdata = LUI.QRcodeCaptured;


        db = FirebaseFirestore.getInstance();

        //식당이름 가져오기
        DocumentReference docRef = db.collection("RestaurantList").document(QRdata);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        System.out.println("DocumentSnapshot data: " + document.getData());
                        String temp = document.getString("Name");
                        System.out.println(temp);
                        rn.setText(temp);
                    } else {
                        System.out.println("No such document");
                        alert.setTitle("Error");
                        alert.setMessage("QR코드가 올바르지 않습니다.");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        alert.show();

                    }
                } else {
                    System.out.println("get failed with ");
                    alert.setTitle("Error");
                    alert.setMessage("알 수 없는 오류가 발생하였습니다..");
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alert.show();
                }
            }
        });

        //대기 인수 가져오기
        db.collection("RestaurantList").document(QRdata).collection("WaitList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                            }
                            wn.setText("현재 대기 팀 : " + count + "팀");
                        } else {
                            System.out.println("Error Occured");
                        }
                    }
                });
    }

    public void onClickYesButton(View view){

        //식당 대기표 어디까지 인지 가져오기
        DocumentReference docRef = db.collection("RestaurantList").document(QRdata);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        System.out.println("DocumentSnapshot data: " + document.getData());
                        double tempdouble = document.getDouble("WaitNumber");
                        int tempint = (int) tempdouble;

                        Map<String, Object> user = new HashMap<>();
                        user.put("Email", LUI.email);
                        myNumberDouble = tempint + 1;
                        String myNumber = Integer.toString(myNumberDouble);
                        db.collection("RestaurantList").document(QRdata).collection("WaitList").document(myNumber)
                                .set(user, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(WaitConfirmActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        System.out.println("No such document");
                    }
                } else {
                    System.out.println("get failed with ");
                }

                LoginedUserInformation.WaitingRestaurant = rn.getText().toString();
                LoginedUserInformation.WaitingNumber = myNumberDouble;

                //레스토랑 정보 변경
                Map<String, Object> data = new HashMap<>();
                data.put("Name", rn.getText());
                data.put("WaitNumber", myNumberDouble);
                db.collection("RestaurantList").document(QRdata)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(WaitConfirmActivity.this, "Push Success", Toast.LENGTH_SHORT).show();
                            }
                        });

                //UserInformation의 대기 변경
                Map<String, Object> informationData = new HashMap<>();
                informationData.put("WaitListRestaurantName", LoginedUserInformation.WaitingRestaurant);
                informationData.put("WaitListRestaurantNumber", LoginedUserInformation.WaitingNumber);
                LoginedUserInformation.WaitingRestaurantCode = QRdata;
                informationData.put("WaitListRestaurantCode", LoginedUserInformation.WaitingRestaurantCode);
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

        finish();

    }


}
