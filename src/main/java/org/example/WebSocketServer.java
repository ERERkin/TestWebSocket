package org.example;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    private Set<String> connectedIPs;
    private Random random;

    public WebSocketServer(InetSocketAddress address) {
        super(address);
        connectedIPs = new HashSet<>();
        random = new Random();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String ipAddress = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        if (!connectedIPs.contains(ipAddress)) {
            connectedIPs.add(ipAddress);
            BigInteger randomValue = generateUniqueRandomNumber();
            conn.send("{\"number\": " + randomValue.toString() + "}");
        } else {
            conn.close();
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String ipAddress = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        connectedIPs.remove(ipAddress);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {}

    @Override
    public void onStart() {
        System.out.println("The WebSocket server has been successfully started.");
    }

    private BigInteger generateUniqueRandomNumber() {
        BigInteger randomValue;
        do {
            randomValue = new BigInteger(128, random);
        } while (connectedIPs.contains(randomValue.toString()));
        return randomValue;
    }

    public static void main(String[] args) {
        int port = 8080;
        WebSocketServer server = new WebSocketServer(new InetSocketAddress(port));
        server.start();
        System.out.println("The WebSocket server is running on port: " + port);
    }
}