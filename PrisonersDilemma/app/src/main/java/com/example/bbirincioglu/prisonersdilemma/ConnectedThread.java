package com.example.bbirincioglu.prisonersdilemma;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Message;

import java.io.InputStream;
import java.io.OutputStream;
import android.os.Handler;

/**
 * Created by bbirincioglu on 3/11/2016.
 */
public class ConnectedThread extends Thread {
    private Activity activity;
    private GamePlayActivity.MessageHandler messageHandler;
    private BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ConnectedThread(Activity activity, BluetoothSocket bluetoothSocket, GamePlayActivity.MessageHandler messageHandler) {
        setActivity(activity);
        setMessageHandler(messageHandler);
        setBluetoothSocket(bluetoothSocket);
        InputStream temp1 = null;
        OutputStream temp2 = null;

        try {
            temp1 = bluetoothSocket.getInputStream();
            temp2 = bluetoothSocket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputStream = temp1;
        outputStream = temp2;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = getInputStream().read(buffer);
                Message message = Message.obtain();
                message.arg1 = bytes;
                message.obj = buffer;
                getMessageHandler().sendMessage(message);
            } catch (Exception e) {
                break;
            }
        }
    }

    public void write(String message) {
        byte[] messageAsByteArray = message.getBytes();

        try {
            getOutputStream().write(messageAsByteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelConnection() {
        try {
            getBluetoothSocket().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public GamePlayActivity.MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(GamePlayActivity.MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}
