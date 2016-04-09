package com.game.wargame.Controller.Engine;

import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Players.LocalPlayerModel;
import com.game.wargame.Model.Entities.Players.Player;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.Views.GameView;

/**
 * Created by sergei on 05/04/16.
 */
public class DisplayCallback implements IDisplayCallback {

    private EntitiesModel mEntities;
    private LocalPlayerModel mCurrentPlayer;
    private GameView mGameView;
    private GameContext mGameContext;

    public DisplayCallback(GameView gameView, GameContext gameContext, LocalPlayerModel currentPlayer, EntitiesModel entities) {
        mGameView = gameView;
        mGameContext = gameContext;
        mCurrentPlayer = currentPlayer;
        mEntities = entities;
    }

    @Override
    public void display() {
        mGameView.display(mEntities);
        mGameView.display(mCurrentPlayer);
        mGameView.display(mGameContext);
    }
}