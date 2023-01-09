package com.example.androidbarberstaffapp.Interface;

import com.example.androidbarberstaffapp.Model.Salon;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Salon> salonList);
    void onBranchLoadFailed(String message);
}
