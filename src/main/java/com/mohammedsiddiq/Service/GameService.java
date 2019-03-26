package com.mohammedsiddiq.Service;

import com.mohammedsiddiq.Configs.Configuration;
import com.mohammedsiddiq.DTOs.MakeMove;
import com.mohammedsiddiq.DTOs.Move;
import com.mohammedsiddiq.DTOs.Response;
import com.mohammedsiddiq.DTOs.StartGameResponse;
import com.mohammedsiddiq.RestClient.GameClient;
import com.mohammedsiddiq.EngineInterface.IChessEngine;
import com.mohammedsiddiq.EngineInterface.Session;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.petero.cuckoo.engine.chess.ComputerPlayer;
import org.petero.cuckoo.engine.chess.Game;
import org.petero.cuckoo.engine.chess.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.HashMap;

@Service
public class GameService implements IChessEngine {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    boolean done = false;


    //Reading required configs
    private String opponentsEndPoint = Configuration.OPPONENTS_END_POINT;
    private String playerName = Configuration.USER_NAME;
    private String playAs = Configuration.PLAY_AS;

    private Retrofit retrofit = new Retrofit.Builder().baseUrl(opponentsEndPoint).addConverterFactory(GsonConverterFactory.create()).build();
    private GameClient gameClient = retrofit.create(GameClient.class);


    //Map to keep track of multiple game instances
    private HashMap<Integer, ChessEngineService> gameToChessEngineMap = new HashMap<>();


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
            ComputerPlayer.engineName = "white" + " - " + playerName;

            whitePlayer.timeLimit(1, 1, false);
            blackPlayer = new RestBasedOpponentPlayer(userName);
            ((ComputerPlayer) whitePlayer).setTTLogSize(2);
            chessEngineService = new ChessEngineService(whitePlayer, blackPlayer);
            gameToChessEngineMap.put(session.getSessionId(), chessEngineService);

            logger.debug("Making my first move..");

            Move myMove = chessEngineService.makeMyNextMove();
            response.setMyFirstMove(myMove);
        } else {

            logger.debug("Intializing game.. with Rest user as white player");
            blackPlayer = new ComputerPlayer();
            ComputerPlayer.engineName = "black" + " - " + playerName;
            blackPlayer.timeLimit(1, 1, false);
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
        chessEngineService.printChessBoard();
        if (opponentResponse.getMyMove().equals("-") || opponentResponse.getMyMove().equals("Invalid Move")) {
            return opponentResponse;
        }

        //TODO : Persist the responseMove to the DB

        System.out.println("Chess board for session ID " + sessionID);
        Move myMove = chessEngineService.makeMyNextMove();
        chessEngineService.printChessBoard();
        return myMove;

    }


    private StartGameResponse getOpponentsFirstMove(boolean firstMove) throws IOException {


        Call<StartGameResponse> request = gameClient.startNewGame(playerName, firstMove);

        return request.execute().body();

    }


    //Makes an API call with current move and returns the responded move
    private Move getOpponentsNextMove(MakeMove myMove) throws IOException {

        Call<Move> request = gameClient.makeNextMove(myMove);
        retrofit2.Response<Move> response = request.execute();

        //If opponents killed itself assume that he has resigned
        if (response.isSuccessful())
            return response.body();
        return new Move("resign");

    }


    public void play(ChessEngineService chessEngineService, Move myMove, int session) throws IOException {

        MakeMove nextMove;
        Move opponentsMove;
        while (chessEngineService.getCurrentGameState() == Game.GameState.ALIVE) {


            //waiting for other players move

            logger.debug("Getting opponents Move");
            nextMove = new MakeMove();
            nextMove.setGameId(session);
            nextMove.setMove(myMove);

            opponentsMove = getOpponentsNextMove(nextMove);
            chessEngineService.makeOpponentsNextMove(opponentsMove);

            logger.debug("Making my move");
            myMove = chessEngineService.makeMyNextMove();

        }
        // Once the game is done final move/state must be sent
        nextMove = new MakeMove();
        nextMove.setGameId(session);
        nextMove.setMove(myMove);
        getOpponentsNextMove(nextMove);

    }

    public void autoPlay() throws IOException {
        ChessEngineService chessEngineService;
        Move myMove;
        int session;

        ComputerPlayer.engineName = playAs + " - " + playerName;

        if (playAs.equals("white")) {

            logger.info("Starting the game as white");
            Player whitePlayer = new ComputerPlayer();
            whitePlayer.timeLimit(2, 2, false);

            Player blackPlayer = new RestBasedOpponentPlayer();
            chessEngineService = new ChessEngineService(whitePlayer, blackPlayer);

            StartGameResponse opponentsResponse = getOpponentsFirstMove(false);
            session = opponentsResponse.getSession().getSessionId();

            myMove = chessEngineService.makeMyNextMove();


            //Continue playing with the opponent
            play(chessEngineService, myMove, session);


        } else {

            logger.info("Starting the game as black");

            Player blackPlayer = new ComputerPlayer();
            blackPlayer.timeLimit(2, 2, false);
            RestBasedOpponentPlayer whitePlayer = new RestBasedOpponentPlayer();
            chessEngineService = new ChessEngineService(whitePlayer, blackPlayer);

            // Making first moves

            StartGameResponse opponentsResponse = getOpponentsFirstMove(true);
            chessEngineService.makeOpponentsNextMove(opponentsResponse.getMyFirstMove());
            session = opponentsResponse.getSession().getSessionId();
            logger.debug("Making first move");
            myMove = chessEngineService.makeMyNextMove();


            //Continue playing with the opponent
            play(chessEngineService, myMove, session);


        }

    }
}
