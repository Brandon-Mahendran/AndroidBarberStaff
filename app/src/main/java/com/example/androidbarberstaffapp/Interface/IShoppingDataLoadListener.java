package com.example.androidbarberstaffapp.Interface;

import com.example.androidbarberstaffapp.Model.ShoppingItem;

import java.util.List;

public interface IShoppingDataLoadListener {
    //Copy from Client app
    void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList);
    void onShoppingDataLoadFailed(String message);
}
