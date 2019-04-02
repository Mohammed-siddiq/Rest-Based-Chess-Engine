package com.mohammedsiddiq.ChessGame.Service;

import com.mohammedsiddiq.ChessGame.DTOs.Move;
import com.mohammedsiddiq.ChessGame.DTOs.Response;
import com.mohammedsiddiq.ChessGame.DTOs.StartGameResponse;
import com.mohammedsiddiq.ChessGame.EngineInterface.*;
import org.petero.cuckoo.engine.chess.ComputerPlayer;
import org.petero.cuckoo.engine.chess.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class GameService implements IChessEngine {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    boolean done = false;


    //Map to keep track of multiple game instances
    HashMap<Integer, ChessEngineService> gameToChessEngineMap = new HashMap<>();


    @Override
    public StartGameResponse newGame(String userName, boolean firstMove) {


        // create a new session object persist on the DB and send the session response

        logger.debug("Creating a new Session for this game");
        Session session = new Session();
        session.setUserName(userName);
        session.setSessionId(session.hashCode());

        ChessEngineService chessEngineService;

        StartGameResponse response = new StartGameResponse();
        response.setFirstMove(firstMove);
        response.setYourOpponentName("Alpha");
        response.setSession(session);

        Player blackPlayer;
        Player whitePlayer;

        //TODO : persist the session on DB

        if (firstMove) {
            // Start the game with Computer as white and human as black palyer

            logger.debug("Intializing game.. with Rest user as black player");

            whitePlayer = new ComputerPlayer();
            whitePlayer.timeLimit(1, 1, false);
            blackPlayer = new RestBasedOpponentPlayer(userName);
            ((ComputerPlayer) whitePlayer).setTTLogSize(2);
            chessEngineService = new ChessEngineService(whitePlayer, blackPlayer);
            gameToChessEngineMap.put(session.getSessionId(), chessEngineService);

            logger.debug("Making my first move..");

            Move myMove = chessEngineService.makeMyNextMove();
            myMove.setPlayer("White: " + "Alpha");
            response.setMyFirstMove(myMove);
        } else {

            logger.debug("Intializing game.. with Rest user as white player");
            blackPlayer = new ComputerPlayer();
            whitePlayer = new RestBasedOpponentPlayer(userName);
            ((ComputerPlayer) blackPlayer).setTTLogSize(2);
            chessEngineService = new ChessEngineService(whitePlayer, blackPlayer);
            gameToChessEngineMap.put(session.getSessionId(), chessEngineService);

        }

        return response;
    }

    @Override
    public Response quit(Session session) {

        //TODO : Update the session on the DB as quit

        //TODO: Terminate the session with the chess engine and

        Response response = new Response();


        //Getting the right instance of the game;

        ChessEngineService chessEngineService = gameToChessEngineMap.get(session.getSessionId());
        if (chessEngineService.quitGame())
            response.setMessage("Successfully terminated the game for the opponent " + session.getUserName());
        else response.setMessage("No game exist for the opponent " + session.getUserName());

        return response;
    }

    @Override
    public Move move(Move move, int sessionID) {

        //TODO : Persist the move to the DB

        //TODO : send the move to the Chess engine, wait for the response


        //Getting the right instance of the game;
        ChessEngineService chessEngineService = gameToChessEngineMap.get(sessionID);

        Move opponentResponse = chessEngineService.makeOpponentsNextMove(move);
        if (opponentResponse.getMyMove().equals("-") || opponentResponse.getMyMove().equals("Invalid Move")) {
            return opponentResponse;
        }

        //TODO : Persist the responseMove to the DB

        System.out.println("Chess board for session ID " + sessionID);
        Move myMove = chessEngineService.makeMyNextMove();
        chessEngineService.printChessBoard();
        return myMove;

    }
}
