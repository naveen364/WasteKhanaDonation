package com.bnp.wastekhanadonation;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.Observable;

public class myadapter extends RecyclerView.Adapter<myadapter.myviewholder>
{
    ArrayList<model> datalist;
    FirebaseAuth fAuth= FirebaseAuth.getInstance();
    public String userID = fAuth.getCurrentUser().getUid();
    public String uid;
    FirebaseFirestore firestore;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference notebookref = db.collection("user data");
    private AdapterView.OnItemClickListener listener;


    public myadapter(ArrayList<model> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow, parent, false);
        return new myviewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, @SuppressLint("RecyclerView") int position) {
        model m = datalist.get(position);
        holder.tname.setText(datalist.get(position).getName());
        holder.ttype.setText(datalist.get(position).getType());
        holder.tfoodname.setText(datalist.get(position).getFood_item());
        RequestOptions myOptions = new RequestOptions()
                .fitCenter() // or centerCrop
                .override(200, 200);
        Glide.with(holder.tdescription.getContext()).load(datalist.get(position).getFood_image()).apply(myOptions).into(holder.tfoodpic);
        holder.tdescription.setText(datalist.get(position).getDescription());
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notebookref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot snapshot: task.getResult()) {
                            //still it delete all child of user data
                            snapshot.getReference().delete();
                        }
                    }
                });
                datalist.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    public void deleteItem(int position ,View v,String naam){

        //getSnapshots().getSnapshot(position).getReference().delete();
        //notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        TextView tname,ttype,tdescription,tfoodname;
        ImageView tfoodpic;
        CardView del;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            tname = itemView.findViewById(R.id.name);
            ttype = itemView.findViewById(R.id.type);
            del = itemView.findViewById(R.id.delete);
            tfoodname = itemView.findViewById(R.id.foodname);
            tfoodpic = itemView.findViewById(R.id.foodpic);
            tdescription = itemView.findViewById(R.id.description);
        }
    }
}
