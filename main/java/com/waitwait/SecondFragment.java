package com.waitwait;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {

    FirebaseFirestore db;

    TextView tv1;
    TextView tv2;

    public static boolean ising = false;


    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        tv1 = view.findViewById(R.id.callStatusTextView);
        tv2 = view.findViewById(R.id.isCallingNowTextView);

        db = FirebaseFirestore.getInstance();

        ising = true;


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(ising){
                    try{
                        Thread.sleep(5000);
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }

                    DocumentReference docRef = db.collection("UserInformation").document(LoginedUserInformation.email);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    System.out.println("DocumentSnapshot data: " + document.getData());
                                    if(document.getString("CallListRestaurantName").equals("none")){
                                        //식당 대기 없을 때
                                        tv1.setText("기다리는 음식이 없어요!");
                                        tv2.setText("");
                                    }else{
                                        //식당 대기 있을 때
                                        String waitingRestaurantId = document.getString("CallListRestaurantName");
                                        DocumentReference docRef2 = db.collection("RestaurantList").document(waitingRestaurantId);
                                        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document2 = task.getResult();
                                                tv1.setText(document2.getString("Name"));
                                                tv2.setText("음식을 준비중이에요! 기다려주세요!");
                                            }
                                        });

                                    }
                                } else {
                                    System.out.println("No such document");
                                    tv1.setText("기다리는 음식이 없어요!");
                                    tv2.setText("");
                                }
                            } else {
                                System.out.println("get failed with ");
                                tv1.setText("기다리는 음식이 없어요!");
                                tv2.setText("");
                            }
                        }
                    });
                }
            }
        }).start();

        return view;

    }

    public void stopThread(){
        ising = false;
    }
}
