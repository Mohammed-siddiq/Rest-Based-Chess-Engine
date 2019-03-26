package com.mohammedsiddiq.EngineInterface;

import com.mohammedsiddiq.DTOs.Move;
import com.mohammedsiddiq.DTOs.Response;
import com.mohammedsiddiq.DTOs.StartGameResponse;

public interface IChessEngine {
    StartGameResponse newGame(String userName, boolean firstMove);

    Move move(Move move, int sessionId);

    Response quit(Session sessionId);
}
