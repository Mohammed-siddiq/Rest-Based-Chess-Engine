package com.mohammedsiddiq.DTOs;

public class MakeMove {
    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    Move move;
    int gameId;
}
