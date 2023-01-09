package com.example.androidbarberstaffapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbarberstaffapp.Common.Common;
import com.example.androidbarberstaffapp.DoneServicesActivity;
import com.example.androidbarberstaffapp.Interface.IRecyclerItemSelectedListener;
import com.example.androidbarberstaffapp.Model.BookingInformation;
import com.example.androidbarberstaffapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {
    Context context;

    List<BookingInformation> timeSlotList;
    List<CardView> cardViewList;


    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<BookingInformation> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(i)).toString());
        if(timeSlotList.size() == 0) // If all position is available , just show list
        {
            myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

            myViewHolder.txt_time_slot_description.setText("Available");
            myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
            myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));

            //Add Event nothing
            myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                @Override
                public void onItemSelected(View view, int position) {
                    //Fix crash if we not add this function
                }
            });

        }
        else
        {
            for(BookingInformation slotValue : timeSlotList)
            {
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if(slot == i) // If slot == position
                {

                    if (!slotValue.isDone()) {

                        myViewHolder.card_time_slot.setTag(Common.DISABLE_TAG);
                        myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));

                        myViewHolder.txt_time_slot_description.setText("Full");
                        myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                        myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));

                        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                            @Override
                            public void onItemSelected(View view, int position) {
                                //Only add for gray time slot
                                //Here we will get Booking Information and store in Common.currentBookingInformation
                                //After that , start DoneServicesActivity
                                FirebaseFirestore.getInstance()
                                        .collection("AllSalon")
                                        .document(Common.state_name)
                                        .collection("Branch")
                                        .document(Common.selected_salon.getSalonId())
                                        .collection("Barber")
                                        .document(Common.currentBarber.getBarberId())
                                        .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                                        .document(slotValue.getSlot().toString())
                                        .get()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                Common.currentBookingInformation = task.getResult().toObject(BookingInformation.class);
                                                Common.currentBookingInformation.setBookingId(task.getResult().getId());
                                                context.startActivity(new Intent(context, DoneServicesActivity.class));
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                    else
                    {
                        //If service is done
                        myViewHolder.card_time_slot.setTag(Common.DISABLE_TAG);
                        myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));

                        myViewHolder.txt_time_slot_description.setText("Done");
                        myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                        myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));

                        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                            @Override
                            public void onItemSelected(View view, int position) {
                                //Add here to fix crash
                            }
                        });

                    }
                }
                else
                {
                    //Fix crash
                    if(myViewHolder.getiRecyclerItemSelectedListener() == null)
                    {
                        //We only add event for view holder whitch is not implement click
                        //Because if we dont put this if condition
                        //All time slot with slot value higher current time slot will be override event

                        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                            @Override
                            public void onItemSelected(View view, int position) {

                            }
                        });
                    }
                }
            }
        }

        //Add all card to list (20 card because we have 20 time slot)
        //No add card already in cardViewList
        if(!cardViewList.contains(myViewHolder.card_time_slot))
            cardViewList.add(myViewHolder.card_time_slot);


    }


    @Override
    public int getItemCount() {
        return Common.TOTAL_TIME_SLOT;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_time_slot,txt_time_slot_description;
        CardView card_time_slot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public IRecyclerItemSelectedListener getiRecyclerItemSelectedListener() {
            return iRecyclerItemSelectedListener;
        }

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_time_slot = (CardView)itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = (TextView)itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description = (TextView)itemView.findViewById(R.id.txt_time_slot_description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelected(view,getAdapterPosition());
        }
    }
}
