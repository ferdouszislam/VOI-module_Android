package com.example.voimoduleapp;

import com.sinch.android.rtc.calling.Call;

public interface VoiHandler {

    void setUpClient();
    void terminateClient();
    void callUser(String callRecipient);
    void hangUpCall();
    void answerIncomingCall();

    // interface for class/presenter/activity who instantiated VoiHandler
    interface Master{

        void onCallerCalling();
        void onCallConnected(String callerId);
        void onCallDisconnected();
        void onCallIncoming(String callerId);

        void onSuccess(String message);
        void onFailed(String message);


    }

}
