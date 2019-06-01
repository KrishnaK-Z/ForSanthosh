package com.example.mcadapp.Chats.Sockets;

import android.app.Application;

import com.example.mcadapp.Utils.Config;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class WebSocket {

    private Socket websocket;

    public WebSocket(){
        try {
            websocket = IO.socket(Config.HOST);

        }
        catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public Socket getWebsocket() {
        return websocket;
    }
}
