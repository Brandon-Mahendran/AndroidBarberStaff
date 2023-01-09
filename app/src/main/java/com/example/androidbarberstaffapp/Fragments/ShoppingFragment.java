package com.example.androidbarberstaffapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.androidbarberstaffapp.Adapter.MyShoppingItemAdapter;
import com.example.androidbarberstaffapp.Common.SpacesItemDecoration;
import com.example.androidbarberstaffapp.DoneServicesActivity;
import com.example.androidbarberstaffapp.Interface.IOnShoppingItemSelected;
import com.example.androidbarberstaffapp.Interface.IShoppingDataLoadListener;
import com.example.androidbarberstaffapp.Model.ShoppingItem;
import com.example.androidbarberstaffapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShoppingFragment extends BottomSheetDialogFragment implements IShoppingDataLoadListener, IOnShoppingItemSelected {

    Unbinder unbinder;

    IOnShoppingItemSelected callBackToActivity;

    IShoppingDataLoadListener iShoppingDataLoadListener;

    CollectionReference shoppingItemRef;

    @BindView(R.id.chip_group)
    ChipGroup chipGroup;
    @BindView(R.id.chip_wax)
    Chip chip_wax;


    @OnClick(R.id.chip_wax)
    void waxLoadClick(){
        setSelectedChip(chip_wax);
        loadShoppingItem("Wax");
    }

    @BindView(R.id.chip_spray)
    Chip chip_spray;
    @OnClick(R.id.chip_spray)
    void sprayLoadClick(){
        setSelectedChip(chip_spray);
        loadShoppingItem("Spray");
    }

    @BindView(R.id.chip_hair_care)
    Chip chip_hair_care;
    @OnClick(R.id.chip_hair_care)
    void hairCareLoadClick(){
        setSelectedChip(chip_hair_care);
        loadShoppingItem("HairCare");
    }

    @BindView(R.id.chip_body_care)
    Chip chip_body_care;
    @OnClick(R.id.chip_body_care)
    void bodyCareLoadClick(){
        setSelectedChip(chip_body_care);
        loadShoppingItem("BodyCare");
    }


    @BindView(R.id.recycler_items)
    RecyclerView recycler_items;


    private static ShoppingFragment instance;

    public static ShoppingFragment getInstance(IOnShoppingItemSelected iOnShoppingItemSelected){
        return instance==null?new ShoppingFragment(iOnShoppingItemSelected):instance;
    }


    private void loadShoppingItem(String itemMenu) {
        //Copy from Client app (ShoppingFragment)

        shoppingItemRef = FirebaseFirestore.getInstance().collection("Shopping")
                .document(itemMenu)
                .collection("Items");
        //Get Data
        shoppingItemRef.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iShoppingDataLoadListener.onShoppingDataLoadFailed(e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<ShoppingItem> shoppingItems = new ArrayList<>();
                    for(DocumentSnapshot itemSnapshot:task.getResult())
                    {
                        ShoppingItem shoppingItem = itemSnapshot.toObject(ShoppingItem.class);
                        shoppingItem.setId(itemSnapshot.getId()); // Remember add it if you don't want to get null reference
                        shoppingItems.add(shoppingItem);
                    }
                    iShoppingDataLoadListener.onShoppingDataLoadSuccess(shoppingItems);
                }
            }
        });

    }

    private void setSelectedChip(Chip chip) {
        //Copy from Client app (ShoppingFragment)


        //Set color
        for(int i=0;i<chipGroup.getChildCount();i++)
        {
            Chip chipItem = (Chip)chipGroup.getChildAt(i);
            if(chipItem.getId() != chip.getId()) // If not selected
            {
                chipItem.setChipBackgroundColorResource(android.R.color.darker_gray);
                chipItem.setTextColor(getResources().getColor(android.R.color.white));
            }
            else // if selected
            {
                chipItem.setChipBackgroundColorResource(android.R.color.holo_orange_dark);
                chipItem.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    public ShoppingFragment(IOnShoppingItemSelected callBackToActivity) {
        this.callBackToActivity = callBackToActivity;
    }

    public ShoppingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_shopping, container, false);

        unbinder = ButterKnife.bind(this,itemView);

        //Default load
        loadShoppingItem("Wax");

        init();
        initView();

        return itemView;
    }

    private void initView() {
        recycler_items.setHasFixedSize(true);
        recycler_items.setLayoutManager(new GridLayoutManager(getContext(),2));
        recycler_items.addItemDecoration(new SpacesItemDecoration(8));
    }

    private void init() {
        iShoppingDataLoadListener = this;
    }

    @Override
    public void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList) {
        MyShoppingItemAdapter adapter = new MyShoppingItemAdapter(getContext(),shoppingItemList,this);
        recycler_items.setAdapter(adapter);
    }

    @Override
    public void onShoppingDataLoadFailed(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShoppingItemSelected(ShoppingItem shoppingItem) {

        callBackToActivity.onShoppingItemSelected(shoppingItem);


    }
}
