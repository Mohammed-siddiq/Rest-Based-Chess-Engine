package com.mohammedsiddiq.DTOs;

public class Move {


    public Move() {
    }

    public Move(String myMove) {
        this.myMove = myMove;
    }

    public String getMyMove() {
        return myMove;
    }

    public void setMyMove(String myMove) {
        this.myMove = myMove;
    }
    //TODO : Add appropriate fields according to the engine

    String myMove;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    String player;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    private String gameStatus;

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }
}
