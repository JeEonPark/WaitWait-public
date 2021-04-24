package com.waitwait;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpActivity2 extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPassword2;
    private EditText editTextName;
    private EditText editTextPhone;

    private String email = "";
    private String password = "";
    private String password2 = "";
    private String name = "";
    private String phone = "";

    private TextView errorMsg;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.emailWord);
        editTextPassword = findViewById(R.id.pwWord);
        editTextPassword2 = findViewById(R.id.pwWord2);
        editTextName = findViewById(R.id.nameWord);
        editTextPhone = findViewById(R.id.phoneWord);
        errorMsg = findViewById(R.id.errorMessage);

        db = FirebaseFirestore.getInstance();

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

    }


    private void createUser(String email1, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email1, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            Map<String, Object> user = new HashMap<>();
                            user.put("Email", email);
                            user.put("Name", name);
                            user.put("Phone", phone);
                            user.put("WaitListRestaurantName", "none");
                            user.put("WaitListRestaurantNumber", 0);
                            user.put("WaitListRestaurantCode", "none");
                            user.put("CallListRestaurantName", "none");
                            user.put("CallListCallNow", "false");


                            db.collection("UserInformation").document(email)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUpActivity2.this, "Success", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            Toast.makeText(SignUpActivity2.this, "성공", Toast.LENGTH_SHORT).show();
                        } else {
                            // 회원가입 실패
                            Toast.makeText(SignUpActivity2.this, "실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    public void signUp(View view) {
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        password2 = editTextPassword2.getText().toString();
        name = editTextName.getText().toString();
        phone = editTextPhone.getText().toString();


        if(isValidEmail() == 2 && isValidPasswd() == 2 && isPasswdSame() == 2 && isName() == 2 && isPhone() == 2) {
            createUser(email, password);



        } else if(isValidEmail() == 0) {
            //이메일 공백
            errorMsg.setText("이메일을 입력해주세요.");
        } else if(isValidPasswd() == 0) {
            //비밀번호 공백
            errorMsg.setText("비밀번호를 입력해주세요.");
        } else if(isPasswdSame() == 0) {
            //비밀번호 확인란 공백
            errorMsg.setText("비밀번호 확인란을 입력해주세요.");
        } else if(isName() == 0) {
            //이름 공백
            errorMsg.setText("이름을 입력해주세요.");
        } else if(isPhone() == 0) {
            //전화번호 공백
            errorMsg.setText("전화번호를 입력해주세요.");
        } else if (isValidEmail() == 1){
            //이메일 형식 불일치
            errorMsg.setText("이메일 형식을 확인해주세요.");
        } else if (isValidPasswd() == 1){
            //비밀번호 형식 불일치
            errorMsg.setText("비밀번호 형식을 확인해주세요.");
        } else if (isPasswdSame() == 1){
            //비밀번호 확인 불일치
            errorMsg.setText("비밀번호가 일치하지 않습니다.");
        }
    }

    // 이메일 유효성 검사
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

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{6,16}$");

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

    //비밀번호 확인 맞음?
    private int isPasswdSame() {
        if(password2.isEmpty()){
            // 비밀번호 공백
            return 0;
        } else if(!password.equals(password2)){
            // 비밀번호 일치하지 않음
            return 1;
        } else {
            // 성공
            return 2;
        }
    }

    private int isName(){
        if(name.isEmpty()) {
            //이름 공백
            return 0;
        } else {
            //성공
            return 2;
        }
    }

    private int isPhone() {
        if (phone.isEmpty()) {
            //전화번호 공백
            return 0;
        } else {
            //성공
            return 2;
        }
    }



}
