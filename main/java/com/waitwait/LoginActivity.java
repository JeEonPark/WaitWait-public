package com.waitwait;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    // 이메일과 비밀번호
    private EditText editTextEmail;
    private EditText editTextPassword;

    private String email = "";
    private String password = "";

    private TextView errorMsg;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //firebase 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.idWord);
        editTextPassword = findViewById(R.id.pwWord);
        errorMsg = findViewById(R.id.errorMessage);

        db = FirebaseFirestore.getInstance();

    }

    private void loginUser(final String email, String password) {//로그인 버튼 눌렀을 때 실행하는 객체
        errorMsg.setText("로그인 중..");
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            LoginedUserInformation.email = email;
                            DocumentReference docRef = db.collection("UserInformation").document(email);

                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            System.out.println("DocumentSnapshot data: " + document.getData());
                                            LoginedUserInformation.name = document.getString("Name");
                                            LoginedUserInformation.phone = document.getString("Phone");
                                            LoginedUserInformation.WaitingRestaurant = document.getString("WaitListRestaurantName");
                                            LoginedUserInformation.WaitingRestaurantCode = document.getString("WaitListRestaurantCode");
                                            LoginedUserInformation.CallListRestaurantName = document.getString("CallListRestaurantName");
                                            LoginedUserInformation.CallListCallNow = document.getString("CallListCallNow");
                                            double tempNumber;
                                            tempNumber = document.getDouble("WaitListRestaurantNumber");
                                            LoginedUserInformation.WaitingNumber = (int) tempNumber;
                                        } else {
                                            System.out.println("No such document");

                                        }
                                    } else {
                                        System.out.println("get failed with ");
                                    }
                                }
                            });

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // 로그인 실패
                            errorMsg.setText("아이디 또는 비밀번호를 확인해주세요.");
                        }
                    }
                });
    }

    public void signInButton(View view) {


        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();

        if (isValidEmail() == 2 && isValidPasswd() == 2) {
            loginUser(email, password);


        } else if (isValidEmail() == 0) {
            //이메일 공백
            errorMsg.setText("이메일을 입력해주세요.");
        } else if (isValidPasswd() == 0) {
            //비밀번호 공백
            errorMsg.setText("비밀번호를 입력해주세요.");
        } else if (isValidEmail() == 1) {
            //이메일 형식 불일치
            errorMsg.setText("이메일 형식을 확인해주세요.");
        } else if (isValidPasswd() == 1) {
            //비밀번호 형식 불일치
            errorMsg.setText("비밀번호 형식을 확인해주세요.");
        }
    }



    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{6,16}$");



    //이메일 유효성 검사
    private int isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            return 0;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            return 1;
        } else {
            // 성공
            return 2;
        }
    }

    // 비밀번호 유효성 검사
    private int isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return 0;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            return 1;
        } else {
            // 성공
            return 2;
        }
    }

    //idpw 찾기 눌렀을 시
    public void onClickToFindIdPwActivity(View view){
        Intent intent = new Intent(getApplicationContext(), FindIdPwActivity.class);
        startActivity(intent);
    }

    //회원가입 버튼 눌렀을 시
    public void onClickToSignUpActivity(View view){
        Intent intent = new Intent(getApplicationContext(), SignUpActivity1.class);
        startActivity(intent);
    }
    public void onClickToQRActivity(View view){
        Intent intent = new Intent(getApplicationContext(), QRActivity.class);
        startActivity(intent);
    }

   public void onClickToDistanceActivity(View view){
       Intent intent = new Intent(getApplicationContext(), DistanceActivity.class);
       startActivity(intent);
   }





}
