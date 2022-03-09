package com.bnp.wastekhanadonation;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class History extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookref = db.collection("user data");
    public static final String TAG = "TAG";
    private TextView textViewData;
    FirebaseAuth fAuth;
    public String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        fAuth= FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        loadNotes();

    }

//    public void delete(){
//        notebookref.document(userID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(History.this, "delete success", Toast.LENGTH_SHORT).show();
//                notify();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(History.this, "delete fail", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public void loadNotes() {
        notebookref.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String data="";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());//
                                //Toast.makeText(History.this,document.getId() + " => " + document.getData(),Toast.LENGTH_SHORT).show();
                                if (document.contains("name") && document.contains("description") && document.contains("user type") && document.contains("userid")) {

                                    String name = (String) document.get("name");
                                    String type = (String) document.get("user type");
                                   // String food_item = (String) document.get("food item");
                                    String description = (String) document.get("description");
                                    String Userid = (String) document.get("userid");
                                    String userID = fAuth.getCurrentUser().getUid();
                                    Timestamp ts = (Timestamp) document.get("timestamp");
                                    //Date date = ((Timestamp) document.get("timestamp")).toDate();
                                    String dateandtime=String.valueOf(ts);
                                    //String dateandtime = ts.toString();

                                    if(Userid.equals(userID)) {
                                        //data += "Name: " + name + "\nUser Type: " + type + "\nDescription: " + description + "\nDate & Time: " +date.toString() + "\n\n";
                                        data += "Name: " + name + "\nUser Type: " + type + "\nDescription: " + description +"\n\n";
                                    }
                                    textViewData.setText(data);
                                }
                            }
                            //textViewData.setText(data);
                        } else {
                            Log.d(TAG, "Error fetching data: ", task.getException());
                        }
                    }
                });
    }
}
