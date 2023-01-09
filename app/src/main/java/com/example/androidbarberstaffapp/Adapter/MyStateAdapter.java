package com.example.androidbarberstaffapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbarberstaffapp.Common.Common;
import com.example.androidbarberstaffapp.Interface.IRecyclerItemSelectedListener;
import com.example.androidbarberstaffapp.Model.City;
import com.example.androidbarberstaffapp.R;
import com.example.androidbarberstaffapp.SalonListActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyStateAdapter extends RecyclerView.Adapter<MyStateAdapter.MyViewHolder> {

    Context context;
    List<City> cityList;

    int lastPosition=-1;

    public MyStateAdapter(Context context, List<City> cityList) {
        this.context = context;
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_state,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_state_name.setText(cityList.get(i).getName());

        setAnimation(myViewHolder.itemView,i);

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position) {
                Common.state_name = cityList.get(i).getName();
                context.startActivity(new Intent(context, SalonListActivity.class));
            }
        });
    }

    private void setAnimation(View itemView, int position) {
        if(position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context,
                    android.R.anim.slide_in_left);
            itemView.startAnimation(animation);
            lastPosition = position;
        }
    }


    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_state_name)
        TextView txt_state_name;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelected(view,getAdapterPosition());
        }
    }
}
