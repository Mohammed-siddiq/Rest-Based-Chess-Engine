package com.mohammedsiddiq.ChessGame.Service;

import com.mohammedsiddiq.ChessGame.DTOs.Move;
import com.mohammedsiddiq.ChessGame.RestClient.GameClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.petero.cuckoo.engine.chess.Player;
import org.petero.cuckoo.engine.chess.Position;
import retrofit2.Call;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.List;

public class RestBasedOpponentPlayer implements Player {

    Config config = ConfigFactory.load("default");
    String baseUrl = config.getString("SERVER_END_POINT");
    private String lastCmd = "";

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RestBasedOpponentPlayer() {

    }

    public RestBasedOpponentPlayer(String name) {
        this.name = name;
    }

    //
//    Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).build();
//    GameClient gameClient = retrofit.create(GameClient.class);
    private String computersCurrentMove;

    public String getMyNextMove() {
        return myNextMove;
    }

    public void setMyNextMove(String myNextMove) {
        this.myNextMove = myNextMove;
    }

    private String myNextMove;


    @Override
    public String getCommand(Position position, boolean b, List<Position> list) {

//            String color = position.whiteMove ? "white" : "black";
        String moveStr = getMyNextMove();

        if (moveStr == null)
            return "quit";
        if (moveStr.length() == 0) {
            return lastCmd;
        } else {
            lastCmd = moveStr;
        }
        return moveStr;

    }

    @Override
    public boolean isHumanPlayer() {
        return false;
    }

    @Override
    public void useBook(boolean b) {

    }

    @Override
    public void timeLimit(int i, int i1, boolean b) {

    }

    @Override
    public void clearTT() {

    }

    public String getComputersCurrentMove() {
        return computersCurrentMove;
    }

    public void setComputersCurrentMove(String computersCurrentMove) {
        this.computersCurrentMove = computersCurrentMove;
    }
}
