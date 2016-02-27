package com.game.wargame.Controller;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.game.wargame.Controller.Communication.GameSocket;
import com.game.wargame.Controller.Communication.PlayerSocket;
import com.game.wargame.Controller.Communication.RemotePlayerSocket;
import com.game.wargame.Model.Entities.LocalPlayerModel;
import com.game.wargame.Model.Entities.OnPlayerPositionChangedListener;
import com.game.wargame.Model.Entities.OnPlayerWeaponTriggeredListener;
import com.game.wargame.Model.Entities.PlayerModel;
import com.game.wargame.Model.Entities.RemotePlayerModel;
import com.game.wargame.Views.GameView;
import com.game.wargame.Controller.Sensors.LocationRetriever;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameEngine implements OnPlayerPositionChangedListener, OnPlayerWeaponTriggeredListener, GameSocket.OnPlayerEventListener {

    private static final int WEAPON_TIME = 100;

    private Map<String, PlayerModel> mPlayersById;
    private LocalPlayerModel mCurrentPlayer;

    private GameView mGameView;

    private LocationRetriever mLocationRetriever;

    private GameSocket mGameSocket;

    /**
     * @brief Constructor
     */
    public GameEngine() {
        mPlayersById = new HashMap<>();
    }

    /**
     * @brief Starts the game engine
     */
    public void onStart(GameView gameView, GameSocket gameSocket, LocalPlayerModel localPlayerModel, LocationRetriever locationRetriever) {
        mGameView = gameView;
        mGameSocket = gameSocket;
        mCurrentPlayer = localPlayerModel;
        mLocationRetriever = locationRetriever;

        addPlayer(mCurrentPlayer);
        startSensors();
        initializeView();

        mGameSocket.setOnPlayerEventListener(this);
    }

    /**
     * @brief Stops the game engine
     */
    public void onStop() {
        stopSensors();
    }

    /**
     * @brief Starts the sensors and listen to events
     */
    private void startSensors() {
        //mCompass.start(this);
        mLocationRetriever.start(mCurrentPlayer);
    }

    /**
     * @brief Stops listening to the sensors
     */
    private void stopSensors() {
        //mCompass.stop();
        mLocationRetriever.stop();
    }

    /**
     * @brief Initialize the view
     */
    private void initializeView() {
        mGameView.initialize(new GameView.OnWeaponTargetDefinedListener() {
            @Override
            public void onWeaponTargetDefined(float x, float y) {
                Point targetPositionInScreenCoordinates = new Point();
                targetPositionInScreenCoordinates.set((int) x, (int) y);

                Projection projection = mGameView.getMapProjection();
                LatLng currentPlayerPosition = mCurrentPlayer.getPosition();
                LatLng targetPosition = projection.fromScreenLocation(targetPositionInScreenCoordinates);

                float[] results = new float[1];
                Location.distanceBetween(currentPlayerPosition.latitude, currentPlayerPosition.longitude, targetPosition.latitude, targetPosition.longitude, results);

                float distanceInMeters = results[0];
                Log.d("Distance in meters", "D=" + String.valueOf(distanceInMeters));

                if (distanceInMeters < 1000) {
                    onPlayerWeaponTriggeredListener(mCurrentPlayer, targetPosition.latitude, targetPosition.longitude, WEAPON_TIME);
                    mCurrentPlayer.fire(targetPosition.latitude, targetPosition.longitude, WEAPON_TIME);
                } else {
                    Log.d("GameEngine", "The target is out of range");
                    mGameView.onActionFinished();
                }
            }
        });

        mGameView.setOnGpsButtonClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameView.moveCameraTo(mCurrentPlayer.getPosition(), 4);
            }
        });
    }

    /**
     * @brief Adding a player to the game
     * @param player
     */
    public void addPlayer(PlayerModel player) {
        player.setOnPlayerPositionChangedListener(this);
        player.setOnPlayerWeaponTriggeredListener(this);
        mPlayersById.put(player.getPlayerId(), player);
    }

    public void removePlayer(PlayerModel player) {

    }

    @Override
    public void onPlayerPositionChanged(PlayerModel player) {
        mGameView.movePlayer(player);
    }

    @Override
    public void onPlayerWeaponTriggeredListener(PlayerModel player, double latitude, double longitude, double speed) {
        LatLng source = player.getPosition();
        LatLng destination = new LatLng(latitude, longitude);

        mGameView.triggerWeapon(source, destination, speed);
    }

    public LocalPlayerModel getLocalPlayer() {
        return mCurrentPlayer;
    }

    public int getPlayersCount() {
        return mPlayersById.size();
    }


    @Override
    public void onPlayerJoined(RemotePlayerSocket playerSocket) {
        RemotePlayerModel player = new RemotePlayerModel("name", playerSocket);
        addPlayer(player);
    }

    @Override
    public void onPlayerLeft(RemotePlayerSocket playerSocket) {
        mPlayersById.remove(playerSocket.getPlayerId());
    }
}
