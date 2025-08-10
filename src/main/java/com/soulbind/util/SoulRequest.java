package com.soulbind.util;

import java.util.Random;

public class SoulRequest {

    private Random random = new Random();


    private String requestingPlayer;
    private String receivingPlayer;
    private String ability;
    private int randomID;



    public SoulRequest(String requestingPlayer, String receivingPlayer, String ability) {
        this.requestingPlayer = requestingPlayer;
        this.receivingPlayer = receivingPlayer;
        this.randomID = this.random.nextInt();
        this.ability = ability;
    }


    public int getRandomID() {
        return randomID;
    }

    public String getAbility() {
        return ability;
    }

    public String getReceivingPlayer() {
        return receivingPlayer;
    }

    public String getRequestingPlayer() {
        return requestingPlayer;
    }
}
