package com.bnp.wastekhanadonation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FoodMap extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,  com.google.android.gms.location.LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    SupportMapFragment mapFragment;
    private int REQUEST_CODE = 11;
    ClusterManager clusterManager;
    FirebaseFirestore fStore;
    public static final String TAG = "TAG";
    private FirebaseFirestore cloudstorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_map);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapFragment.getMapAsync(this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        if(Build.VERSION.SDK_INT<16){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            View decor =getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decor.setSystemUiVisibility(uiOptions);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLastLocation = location;
        showLocation();
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

        MarkerOptions markerOptions1 = new MarkerOptions().position(latLng).title("You are here").snippet("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //mMap.addMarker(markerOptions1).showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        //mMap.addMarker(markerOptions1);
    }

    public void showLocation() {
        this.cloudstorage = FirebaseFirestore.getInstance();
        cloudstorage.collection("user data")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("PotentialBehaviorOverride")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());//
                                if (document.contains("location") && document.contains("name") && document.contains("description")) {
                                    GeoPoint location = (GeoPoint) document.get("location");
                                    String title = (String) document.get("name");
                                    String type = (String) document.get("type");
                                    String description = (String) document.get("description");
                                    String u = (String) document.get("food_image");
                                    String phone = (String) document.get("phone");
                                    title = title.replace(" ","_").toLowerCase();


                                    if(type.equals("Donor")) {
                                        Log.d(TAG, String.valueOf(location) + " Success " + title);
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        Timestamp timestamp = (Timestamp) document.get("timestamp");
                                        Date date = timestamp.toDate();
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                        String timy = sdf.format(date);
                                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(description).snippet(title+" "+phone+" "+u+" "+type).icon(bitmapDescriptorfromVector(getApplicationContext(), R.drawable.ic_donor)));
                                        //Marker mark = mMap.addMarker(new MarkerOptions().position(latLng).title(title+"("+type+")").snippet(description).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                    }
                                    else if(type.equals("Receiver")){

                                        Log.d(TAG, String.valueOf(location) + " Success " + title);
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(description).snippet(title+" "+phone+" "+null+" "+type).icon(bitmapDescriptorfromVector(getApplicationContext(), R.drawable.ic_rcv)));
                                    }
                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(@NonNull Marker marker) {
                                            ImageView tpic;
                                            TextView tfname,tphone,tdesc,ttype,ttime;
                                            String raw = marker.getSnippet();
                                            String title =marker.getTitle();
                                            if(raw.isEmpty()){
                                                Toast.makeText(FoodMap.this,"you are here",Toast.LENGTH_SHORT).show();
                                            }else {
                                                String[] s = raw.split("\\s+");
                                                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FoodMap.this,R.style.BottomSheetDialogTheme);
                                                View bottomsheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_bottom_sheet,(LinearLayout) findViewById(R.id.bottomSheetContainer));
                                                tfname = bottomsheetView.findViewById(R.id.fname);
                                                tdesc = bottomsheetView.findViewById(R.id.fdesc);
                                                ttype = bottomsheetView.findViewById(R.id.ftype);
                                                tphone = bottomsheetView.findViewById(R.id.fphone);
                                                tpic = bottomsheetView.findViewById(R.id.fpic);
                                                tfname.setText(s[0]);
                                                tdesc.setText(title);
                                                ttype.setText(s[3]);
                                                tphone.setText(s[1]);
                                                if(s[2] == null){
                                                    tpic.setVisibility(View.GONE);
                                                }else {
                                                    tpic.setVisibility(View.VISIBLE);
                                                    Glide.with(FoodMap.this).load(s[2]).into(tpic);
                                                }
                                                bottomSheetDialog.setContentView(bottomsheetView);
                                                bottomSheetDialog.show();
                                            }
                                            return false;
                                        }
                                    });
                                }
                            }
                        } else {
                            Log.d(TAG, "Error fetching data: ", task.getException());
                        }
                    }
                });

    }

    private BitmapDescriptor bitmapDescriptorfromVector(Context c,int vectorId){
        Drawable vd = ContextCompat.getDrawable(c,vectorId);
        vd.setBounds(0,0,vd.getIntrinsicWidth(),vd.getIntrinsicHeight());
        Bitmap btmap = Bitmap.createBitmap(vd.getIntrinsicWidth(),vd.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(btmap);
        vd.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(btmap);
    }

//    public Bitmap getbitmap(String url){
//        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//        Bitmap bmp =Bitmap.createBitmap(80,80,conf);
//        Canvas canvas = new Canvas(bmp);
//        Paint color = new Paint();
//        color.setTextSize(35);
//        color.setColor(Color.BLACK);
//        Bitmap b;
//        byte[] byteArray = Base64.decode(url,Base64.DEFAULT);
//        canvas.drawBitmap(BitmapFactory.decodeByteArray(byteArray,0,byteArray.length),0,0,color);
//        canvas.drawText("hi",30,40,color);
//        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapFragment.getMapAsync(this);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }
}
