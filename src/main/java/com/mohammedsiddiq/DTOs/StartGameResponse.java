package com.mohammedsiddiq.DTOs;

import com.mohammedsiddiq.EngineInterface.Session;

public class StartGameResponse {


    public String getYourOpponentName() {
        return yourOpponentName;
    }

    public void setYourOpponentName(String yourOpponentName) {
        this.yourOpponentName = yourOpponentName;
    }

    public void setFirstMove(boolean firstMove) {
        this.isFirstMove = firstMove;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    private Session session;
    private String yourOpponentName;
    private boolean isFirstMove;
    private Move firstMove;

    public Move getFirstMove() {
        return firstMove;
    }

    public void setFirstMove(Move firstMove) {
        this.firstMove = firstMove;
    }
}
