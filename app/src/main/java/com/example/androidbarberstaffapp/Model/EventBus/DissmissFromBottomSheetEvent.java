package com.example.androidbarberstaffapp.Model.EventBus;

public class DissmissFromBottomSheetEvent {
    private boolean isButtonClick;

    public DissmissFromBottomSheetEvent(boolean isButtonClick) {
        this.isButtonClick = isButtonClick;
    }

    public boolean isButtonClick() {
        return isButtonClick;
    }

    public void setButtonClick(boolean buttonClick) {
        isButtonClick = buttonClick;
    }
}
