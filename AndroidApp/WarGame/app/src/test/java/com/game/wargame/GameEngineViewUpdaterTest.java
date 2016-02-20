package com.game.wargame;

import android.content.Context;

import com.game.wargame.Communication.GameEngineSocket;
import com.game.wargame.Communication.PlayerSocket;
import com.game.wargame.Entities.LocalPlayerModel;
import com.game.wargame.Entities.RemotePlayerModel;
import com.game.wargame.GameEngine.GameEngine;
import com.game.wargame.GameEngine.GameView;
import com.game.wargame.Sensors.LocationRetriever;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameEngineViewUpdaterTest {

    @Mock
    private GameView mMockGameView;

    @Mock
    private Context mMockContext;

    @Mock
    private GameEngineSocket mMockGameEngineSocket;

    @Mock
    private LocationRetriever mMockLocationRetriever;

    @Mock
    private PlayerSocket mMockPlayerSocket;

    private GameEngine mGameEngine;

    @Before
    public void setUp() {
        mGameEngine = new GameEngine(mMockContext, mMockGameEngineSocket, mMockLocationRetriever);

        when(mMockGameEngineSocket.getLocalPlayerSocket()).thenReturn(mMockPlayerSocket);

        PlayerSocket playerSocket = mMockGameEngineSocket.getLocalPlayerSocket();

        LocalPlayerModel setupLocalPlayer = new LocalPlayerModel("Clement", playerSocket);
        mGameEngine.start(mMockGameView, setupLocalPlayer);
    }

    @Test
    public void testThatPositionOfMovingPlayerIsUpdated() {
        RemotePlayerModel player = new RemotePlayerModel("player_name", mMockPlayerSocket);

        mGameEngine.onPlayerPositionChanged(player);

        verify(mMockGameView).movePlayer(player);
    }

    @Test
    public void testThatAnimationIsStartedInViewWhenPlayerFired() {
        RemotePlayerModel player = new RemotePlayerModel("player_name", mMockPlayerSocket);

        mGameEngine.onPlayerWeaponTriggeredListener(player, 30, 40, 10);

        verify(mMockGameView).triggerWeapon(player.getPosition(), new LatLng(30, 40), 10);
    }

    @Test
    public void testThatViewIsInitializedWhenGameEngineStarts() {

        verify(mMockGameView).initialize(Matchers.<GameView.OnWeaponTargetDefinedListener>any());

    }
}
