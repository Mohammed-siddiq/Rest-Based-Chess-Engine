package com.mohammedsiddiq.DbObjects;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;


public class GameDbo {
    @Id
    private int gameId;
    private String whitePlayer;
    private String blackPlayer;
    private String result;
    private List<String> moves;
    private boolean active;
    private boolean firstMove;

    public boolean isFirstMove() {
        return firstMove;
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public GameDbo() {
        moves = new ArrayList<>();
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(String whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public String getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(String blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<String> getMoves() {
        return moves;
    }

    public void setMoves(List<String> moves) {
        this.moves = moves;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void updateMove(String move) {
        moves.add(move);
    }
}
