package com.single.agora_backend.service;

import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SocketClient {

    public void sendGameStart(String gameCode) {
        try {
            URL url = new URL("http://localhost:8081/start?gameCode=" + gameCode);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.getResponseCode(); // 요청만 보내면 됨
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
