package com.game.wargame.Model.Entities.Players;

import com.game.wargame.AppConstant;
import com.google.android.gms.maps.model.LatLng;


public class Player {


    protected String mPlayerId;
    protected String mPlayerName;
    protected int mHealth = 100;

    protected LatLng mPosition;
    protected float mRotation;

    public Player(String playerId, String playerName) {
        mPlayerId = playerId;
        mPlayerName = playerName;

        mPosition = new LatLng(AppConstant.INITIAL_LATITUDE, AppConstant.INITIAL_LONGITUDE);
        mRotation = 0;
    }

    @Override
    public boolean equals(Object obj) {
        Player that = (Player) obj;
        return mPlayerId == that.mPlayerId;
    }

    public void setPlayerId(String playerId) {
        mPlayerId = playerId;
    }

    public String getPlayerId() {
        return mPlayerId;
    }

    public void setPlayerName(String playerName) {
        mPlayerName = playerName;
    }

    public String getPlayerName() {
        return mPlayerName;
    }

    public void setHealth(int health) throws PlayerException {
        if(health < 0 || health > 100) {
            throw new PlayerException("Health out of bound");
        }
        mHealth = health;
    }

    public int getHealth() {
        return mHealth;
    }

    public void setPosition(LatLng position) {
        mPosition = position;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public void setRotation(float rotation) {
        mRotation = rotation % 360;
    }

    public float getRotation() {
        return mRotation;
    }
}
