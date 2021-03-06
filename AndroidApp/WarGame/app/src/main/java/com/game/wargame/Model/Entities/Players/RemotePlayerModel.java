package com.game.wargame.Model.Entities.Players;

import com.game.wargame.AppConstant;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Model.Entities.Players.PlayerModel;
import com.game.wargame.Views.Animations.AnimationFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by clement on 19/02/16.
 */
public class RemotePlayerModel extends PlayerModel implements RemotePlayerSocket.OnMoveEventListener, RemotePlayerSocket.OnFireEventListener,
                                                              RemotePlayerSocket.OnDieEventListener, RemotePlayerSocket.OnRespawnEventListener,
                                                              RemotePlayerSocket.OnShieldEventListener {

    protected RemotePlayerSocket mPlayerSocket;

    protected OnRemotePlayerPositionUpdated mOnRemotePlayerPositionUpdated;

    public RemotePlayerModel(String playerName, RemotePlayerSocket playerSocket) {
        super(playerSocket.getPlayerId(), playerName);

        mPlayerSocket = playerSocket;

        mPlayerSocket.setOnFireEventListener(this);
        mPlayerSocket.setOnMoveEventListener(this);
        mPlayerSocket.setOnDieEventListener(this);
        mPlayerSocket.setOnRespawnEventListener(this);
        mPlayerSocket.setOnShieldEventListener(this);
    }

    public void setOnRemotePlayerPositionUpdated(OnRemotePlayerPositionUpdated onRemotePlayerPositionUpdated) {
        mOnRemotePlayerPositionUpdated = onRemotePlayerPositionUpdated;
    }

    @Override
    public void onFireEvent(double latitude, double longitude, double speed) {
        if(mOnPlayerWeaponTriggeredListener != null) {
            mOnPlayerWeaponTriggeredListener.onPlayerWeaponTriggeredListener(this, latitude, longitude, speed);
        }
    }

    @Override
    public void onMoveEvent(double latitude, double longitude) {
        mPosition = new LatLng(latitude, longitude);

        if(mOnRemotePlayerPositionUpdated != null) {
            mOnRemotePlayerPositionUpdated.onRemotePlayerPositionChanged(this);
        }
    }

    @Override
    public void onDieEvent(String playerId, String killerId, double time)
    {
        mIsVisible = false;
        mAnimation = AnimationFactory.buildPlayerDeadAnimation(AppConstant.getNumeroFromName(playerId));
        if(mOnPlayerDiedListener != null)
            mOnPlayerDiedListener.onDied(playerId, killerId, time);
    }

    @Override
    public void onRespawnEvent(String playerId, double time) {
        mIsVisible = true;
        mAnimation = AnimationFactory.buildPlayerAliveAnimation(AppConstant.getNumeroFromName(playerId));
        if (mOnPlayerRespawnListener != null)
            mOnPlayerRespawnListener.onRespawn(playerId, time);
    }

    @Override
    public void onShieldEvent(String playerId, boolean isShielded, double time) {
        super.shield();
        if (isShielded())
            mAnimation = AnimationFactory.buildPlayerShieldedAnimation(AppConstant.getNumeroFromName(playerId));
        else
            mAnimation = AnimationFactory.buildPlayerAliveAnimation(AppConstant.getNumeroFromName(playerId));
    }
}
