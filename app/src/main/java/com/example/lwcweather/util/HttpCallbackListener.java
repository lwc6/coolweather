package com.example.lwcweather.util;

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
