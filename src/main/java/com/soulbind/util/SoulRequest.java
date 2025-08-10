package com.soulbind.util;

import java.util.Random;

public class SoulRequest {

    private Random random = new Random();


    private String requestingPlayer;
    private String receivingPlayer;
    private int randomID;



    public SoulRequest(String requestingPlayer, String receivingPlayer) {
        this.requestingPlayer = requestingPlayer;
        this.receivingPlayer = receivingPlayer;
        this.randomID = this.random.nextInt();
    }


    public int getRandomID() {
        return randomID;
    }

    public String getReceivingPlayer() {
        return receivingPlayer;
    }

    public String getRequestingPlayer() {
        return requestingPlayer;
    }
}
