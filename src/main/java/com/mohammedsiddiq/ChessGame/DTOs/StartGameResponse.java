package com.mohammedsiddiq.ChessGame.DTOs;

import com.mohammedsiddiq.ChessGame.EngineInterface.Session;

public class StartGameResponse {


    public String getYourOpponentName() {
        return yourOpponentName;
    }

    public void setYourOpponentName(String yourOpponentName) {
        this.yourOpponentName = yourOpponentName;
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    private Session session;
    private String yourOpponentName;
    boolean firstMove;
    private Move myFirstMove;

    public Move getMyFirstMove() {
        return myFirstMove;
    }

    public void setMyFirstMove(Move myFirstMove) {
        this.myFirstMove = myFirstMove;
    }
}
