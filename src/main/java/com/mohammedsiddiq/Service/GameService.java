package com.mohammedsiddiq.Service;

import com.mohammedsiddiq.Configs.Configuration;
import com.mohammedsiddiq.DTOs.MakeMove;
import com.mohammedsiddiq.DTOs.Move;
import com.mohammedsiddiq.DTOs.Response;
import com.mohammedsiddiq.DTOs.StartGameResponse;
import com.mohammedsiddiq.DbObjects.GameDbo;
import com.mohammedsiddiq.Repository.GameRepository;
import com.mohammedsiddiq.RestClient.GameClient;
import com.mohammedsiddiq.EngineInterface.IChessEngine;
import com.mohammedsiddiq.EngineInterface.Session;
import org.petero.cuckoo.engine.chess.ComputerPlayer;
import org.petero.cuckoo.engine.chess.Game;
import org.petero.cuckoo.engine.chess.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

import java.io.*;
import java.util.HashMap;
import java.util.List;

@Service
public class GameService implements IChessEngine, Serializable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    boolean done = false;


    //Reading required configs
    private String playerName = Configuration.USER_NAME;
    private String playAs = Configuration.PLAY_AS;

    private Retrofit retrofit;
    private GameClient gameClient;


    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
        gameClient = retrofit.create(GameClient.class);
    }


    //Map to keep track of multiple game instances and chess engines
    private HashMap<Integer, ChessEngineService> gameToChessEngineMap = new HashMap<>();

    //Map to keep track of multiple game instances and DB state
    private HashMap<Integer, GameDbo> gametodatabaseObject = new HashMap<>();


    @Override
    public StartGameResponse newGame(String userName, boolean firstMove) {


        // create a new session object persist on the DB and send the session response

        logger.debug("Creating a new Session for this game");
        Session session = new Session();
        session.setUserName(userName);
        session.setSessionId(session.hashCode());

        ChessEngineService chessEngineService;
//        GameDbo gameDbo = new GameDbo();
//        session.setSessionId(gameDbo.getGameId());

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
            ((ComputerPlayer) whitePlayer).verbose = false;
            chessEngineService = new ChessEngineService(whitePlayer, blackPlayer);


            //Updating the map to the newly created game
            gameToChessEngineMap.put(session.getSessionId(), chessEngineService);

            logger.debug("Making my first move..");

            Move myMove = chessEngineService.makeMyNextMove();


            response.setFirstMove(myMove);
        } else {

            logger.debug("Intializing game.. with Rest user as white player");
            blackPlayer = new ComputerPlayer();
            ((ComputerPlayer) blackPlayer).verbose = false;
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


//        GameDbo gameDbo = gametodatabaseObject.get(session.getSessionId());
        if (chessEngineService.quitGame()) {
            response.setMessage("Successfully terminated the game for the opponent " + session.getUserName());

        } else {
            response.setMessage("No game exist for the opponent " + session.getUserName());
        }

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
            ((ComputerPlayer) whitePlayer).verbose = false;
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
            chessEngineService.makeOpponentsNextMove(opponentsResponse.getFirstMove());
            session = opponentsResponse.getSession().getSessionId();
            logger.debug("Making first move");
            myMove = chessEngineService.makeMyNextMove();

            //Continue playing with the opponent
            play(chessEngineService, myMove, session);


        }

    }

    public void loadGameFromDb(GameDbo gameDbo) {
        try {


            if (gameDbo.isActive()) // if game is active
            {

                if (gameDbo.isFirstMove()) {
                    Player whitePlayer = new ComputerPlayer();
                    Player blackPlayer = new RestBasedOpponentPlayer(gameDbo.getBlackPlayer());
                    ChessEngineService chessEngineService = new ChessEngineService(whitePlayer, blackPlayer);
                    gameToChessEngineMap.put(gameDbo.getGameId(), chessEngineService);
//                    updateGameState(chessEngineService, gameDbo.getMoves(), true);

                }


            }


        } catch (Exception ex) {

        }


    }


    void serialize(ChessEngineService chessEngineService, int sessionId) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            FileOutputStream file = new FileOutputStream("output");
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(chessEngineService);

            out.close();
            file.close();

            System.out.println("Object has been serialized");


            FileInputStream filei = new FileInputStream("output");
            ObjectInputStream in = new ObjectInputStream(filei);

            // Method for deserialization of object
            chessEngineService = (ChessEngineService) in.readObject();

            in.close();
            file.close();

            chessEngineService.printChessBoard();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
