package com.game.wargame.Controller.Communication.RabbitMQ;

import com.game.wargame.Controller.Communication.Game.GameManagerSocket;
import com.game.wargame.Controller.Communication.Game.GameSocket;
import com.game.wargame.Controller.Communication.Game.LocalPlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayerSocket;
import com.game.wargame.Controller.Communication.Game.RemotePlayersSocket;
import com.game.wargame.Controller.Communication.ISocket;
import com.game.wargame.Controller.Communication.ISocketFactory;


public class RabbitMQSocketFactory implements ISocketFactory {

    private RabbitMQConnectionThread mConnectionThread;
    private RemotePlayersSocket mRemotePlayersSocket;

    public RabbitMQSocketFactory(RabbitMQConnectionThread connectionThread) {
        mConnectionThread = connectionThread;

        mRemotePlayersSocket = new RemotePlayersSocket(new RabbitMQSocket(mConnectionThread));
    }

    public GameManagerSocket buildGameManagerSocket() {
        return new GameManagerSocket(new RabbitMQSocket(mConnectionThread, "", ""), this);
    }

    @Override
    public GameSocket buildGameSocket(String gameId) {
        return new GameSocket(gameId, new RabbitMQSocket(mConnectionThread, gameId + "_game_room", ""), this);
    }

    @Override
    public RemotePlayerSocket buildRemotePlayerSocket(String gameId, String playerId) {

        RemotePlayerSocket playerSocket = new RemotePlayerSocket(playerId);
        mRemotePlayersSocket.addPlayer(playerSocket);
        return playerSocket;
    }

    @Override
    public LocalPlayerSocket buildLocalPlayerSocket(String gameId, String playerId) {
        return new LocalPlayerSocket(playerId, new RabbitMQSocket(mConnectionThread, gameId + "_game_room", "all_but_" + playerId), this);
    }

    @Override
    public ISocket buildDirectPeerSocket(String gameId, RemotePlayerSocket remotePlayerSocket) {
        return new RabbitMQSocket(mConnectionThread, gameId + "_game_room", "only_" + remotePlayerSocket.getPlayerId());
    }
}
