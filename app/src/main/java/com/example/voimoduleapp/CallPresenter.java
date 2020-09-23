package com.example.voimoduleapp;

import android.content.Context;

public class CallPresenter implements VoiHandler.CallObserver, VoiHandler.CallClientSetupObserver{

    // view
    private CallView callView;

    // VOI handler
    private VoiHandler voiHandler;

    // models
    private boolean onCall = false;
    private String username;

    public CallPresenter(CallView callView, VoiHandler voiHandler, String username) {

        this.callView = callView;
        this.voiHandler = voiHandler;
        this.username = username;

        voiHandler.setCallClientSetupObserver(this);
        voiHandler.setCallObserver(this);

        callView.setUsernameUI(username);
    }

//    public void setupVoi(){
//        voiHandler.setUpClient();
//    }

    public void terminateVoi(){
        voiHandler.terminateClient();
    }

    // presenter methods (business logic B|)
    public void makeCallToUser(String callRecipient){
        voiHandler.callUser(callRecipient);
    }

    public void hangUpThisCall(){
        voiHandler.hangUpCall();
    }

    public void answerTheCall(){
        voiHandler.answerIncomingCall();
    }

    public boolean isOnCall() {
        return onCall;
    }


    // methods for responding to VoiHandler
    @Override
    public void onCallerCalling() {
        // this doesn't get called if no internet directly onDisconnect() is called

        callView.onCallOutgoingUI();
    }

    @Override
    public void onCallIncoming(String callerId) {
        callView.onCallIncomingUI(callerId);
    }

    @Override
    public void onCallConnected(String callerId) {
        callView.onCallConnectedUI(callerId);

        onCall = true;
    }

    @Override
    public void onCallDisconnected() {
        // method gets called when call is hung up
        // or instantly after no internet/receiver isn't active

        callView.onCallDisconnectedUI();

        onCall = false;
    }


    @Override
    public void onSuccess(String message) {
        callView.onSuccess(message);
    }

    @Override
    public void onFailed(String message) {
        callView.onFail(message);

        onCall = false;
    }


    @Override
    public void onClientSetupDone() {

    }

    @Override
    public void onClientStopped() {

    }

    @Override
    public void onClientSetupFailed(String errorMessage) {

    }
}
