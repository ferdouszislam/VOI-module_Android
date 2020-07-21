package com.example.voimoduleapp;

public interface CallView {

    void setUsernameUI(String username);

    void onCallOutgoingUI();
    void onCallIncomingUI(String callerId);
    void onCallConnectedUI(String callerId);
    void onCallDisconnectedUI();

    void onSuccess(String message);
    void onFail(String message);

}
