package com.mohammedsiddiq.EngineInterface;

import com.mohammedsiddiq.DTOs.Move;
import com.mohammedsiddiq.DTOs.Response;
import com.mohammedsiddiq.DTOs.StartGameResponse;

public interface IChessEngine {

    /**
     * Creates a new Game
     * @param userName The player name
     * @param firstMove if true the opponent makes the first move
     * @return startgameResponse obj
     */
    StartGameResponse newGame(String userName, boolean firstMove);

    /**
     * Performs a move on the chess engine for the corresponding session and returns the opponent's move
     * @param move Move to be done
     * @param sessionId which sessions move
     * @return opponent's move
     */
    Move move(Move move, int sessionId);


    /**
     * Quits the game
     * @param sessionId which session to quit
     * @return Success/failure after quitting
     */
    Response quit(Session sessionId);
}
