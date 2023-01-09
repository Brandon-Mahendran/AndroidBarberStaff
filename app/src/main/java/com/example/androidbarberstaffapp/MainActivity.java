package com.example.androidbarberstaffapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.androidbarberstaffapp.Adapter.MyStateAdapter;
import com.example.androidbarberstaffapp.Common.Common;
import com.example.androidbarberstaffapp.Common.SpacesItemDecoration;
import com.example.androidbarberstaffapp.Interface.IOnAllStateLoadListener;
import com.example.androidbarberstaffapp.Model.Barber;
import com.example.androidbarberstaffapp.Model.City;
import com.example.androidbarberstaffapp.Model.Salon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements IOnAllStateLoadListener {

    @BindView(R.id.recycler_state)
    RecyclerView recycler_state;

    CollectionReference allSalonCollection;

    IOnAllStateLoadListener iOnAllStateLoadListener;

    MyStateAdapter adapter;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Dexter.withActivity(this)
                .withPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                })
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        FirebaseInstanceId.getInstance()
                                .getInstanceId()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            Common.updateToken(MainActivity.this,
                                                    task.getResult().getToken());
                                            Log.d("EDMTTOken",task.getResult().getToken());
                                        }
                                    }
                                });


                        Paper.init(MainActivity.this);
                        String user = Paper.book().read(Common.LOGGED_KEY);
                        if(TextUtils.isEmpty(user)) // If user not login before
                        {
                            setContentView(R.layout.activity_main);

                            ButterKnife.bind(MainActivity.this);

                            initView();

                            init();

                            loadAllStateFromFireStore();
                        }
                        else //If User already login
                        {
                            //Auto login start
                            Gson gson = new Gson();
                            Common.state_name = Paper.book().read(Common.STATE_KEY);
                            Common.selected_salon = gson.fromJson(Paper.book().read(Common.SALON_KEY,""),
                                    new TypeToken<Salon>(){}.getType());
                            Common.currentBarber = gson.fromJson(Paper.book().read(Common.BARBER_KEY,""),
                                    new TypeToken<Barber>(){}.getType());

                            Intent intent = new Intent(MainActivity.this,StaffHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();




    }

    private void loadAllStateFromFireStore() {
        dialog.show();

        allSalonCollection
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iOnAllStateLoadListener.onAllStateLoadFailed(e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<City> cities = new ArrayList<>();
                    for(DocumentSnapshot citySnapShot:task.getResult())
                    {
                        City city = citySnapShot.toObject(City.class);
                        cities.add(city);
                    }
                    iOnAllStateLoadListener.onAllStateLoadSuccess(cities);
                }
            }
        });
    }

    private void init() {
        allSalonCollection = FirebaseFirestore.getInstance().collection("AllSalon");
        iOnAllStateLoadListener = this;
        dialog = new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .build();
    }

    private void initView() {
        recycler_state.setHasFixedSize(true);
        recycler_state.setLayoutManager(new GridLayoutManager(this,2));
        recycler_state.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onAllStateLoadSuccess(List<City> cityList) {
        adapter = new MyStateAdapter(this,cityList);
        recycler_state.setAdapter(adapter);

        dialog.dismiss();
    }

    @Override
    public void onAllStateLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        dialog.dismiss();
    }
}
