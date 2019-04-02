package com.mohammedsiddiq.ChessGame.EngineInterface;

import com.mohammedsiddiq.ChessGame.DTOs.Move;
import com.mohammedsiddiq.ChessGame.DTOs.Response;
import com.mohammedsiddiq.ChessGame.DTOs.StartGameResponse;

public interface IChessEngine {
    StartGameResponse newGame(String userName, boolean firstMove);

    Move move(Move move, int sessionId);

    Response quit(Session sessionId);
}
