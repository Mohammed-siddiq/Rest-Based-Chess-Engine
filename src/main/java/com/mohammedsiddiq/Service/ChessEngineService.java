package com.mohammedsiddiq.Service;


import org.petero.cuckoo.engine.chess.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mohammedsiddiq.DTOs.Move;


public class ChessEngineService extends Game {


    private Logger logger = LoggerFactory.getLogger(this.getClass());


    ChessEngineService(Player whitePlayer, Player blackPlayer) {
        super(whitePlayer, blackPlayer);
        handleCommand("new");
    }


    @Override
    protected boolean handleCommand(String moveStr) {
        return super.handleCommand(moveStr);
    }

    GameState getCurrentGameState() {
        String stateStr = getGameStateString();
        if (stateStr.length() > 0) {
            logger.debug("Printing game state: %s%n", stateStr);
            System.out.printf("Printing game state: %s%n", stateStr);
        }
        GameState currentGameState = getGameState();
        if (getGameState() != GameState.ALIVE) {
            activateHumanPlayer();
        }
        return currentGameState;
    }

    void printChessBoard() {
        System.out.print(TextIO.asciiBoard(pos));
    }

    Move makeOpponentsNextMove(Move move) {

        //Get the opponent player
        Player pl = pos.whiteMove ? whitePlayer : blackPlayer;

        Move myMove = new Move();
        if (getCurrentGameState() != GameState.ALIVE) {
            logger.debug("GameDbo is not alive");
            myMove.setMessage(getGameStateString());
            myMove.setMyMove("-");
            return myMove;
        }

        RestBasedOpponentPlayer restBasedOpponentPlayer = (RestBasedOpponentPlayer) pl;

        //set the next move for the Rest Based player under the custom opponent class through the next move
        restBasedOpponentPlayer.setMyNextMove(move.getMyMove());

        myMove.setMyMove(getPlayersCommand(restBasedOpponentPlayer));
        myMove.setMessage("Your Turn!");
        logger.info("Opponent's move {}", myMove.getMyMove());
        return myMove;
    }

    private String getPlayersCommand(Player p) {

        String moveStr = p.getCommand(new Position(pos), haveDrawOffer(), getHistory());

        if (moveStr.equals("quit")) {
            logger.debug("Returning quit");
            return "quit";
        } else {
            boolean ok = processString(moveStr);
            if (!ok) {
                logger.debug("Invalid move entered {}", moveStr);
                return "Invalid Move";
            }
        }
        return moveStr;
    }


    Move makeMyNextMove() {


        Move myMove = new Move();

        //Check if game is alive before making the move
        if (getCurrentGameState() != GameState.ALIVE) {

            logger.debug("GameDbo is not alive");
            myMove.setMessage(getGameStateString());
            myMove.setMyMove("-");
            myMove.setGameStatus(getGameStateString());
            return myMove;
        }

        Player pl = pos.whiteMove ? whitePlayer : blackPlayer;


        String moveStr = getPlayersCommand(pl);


        myMove.setMyMove(moveStr);
        myMove.setMessage("Your Turn!");
        myMove.setPlayer(ComputerPlayer.engineName);
        myMove.setGameStatus("Alive");
        logger.info(ComputerPlayer.engineName + "'s move {}", myMove.getMyMove());
        return myMove;
    }

    boolean quitGame() {
        Player pl = pos.whiteMove ? whitePlayer : blackPlayer;
        if (getCurrentGameState() != GameState.ALIVE) {
            return false;
        }
        //Set the user's state to resign as he is quitting
        makeOpponentsNextMove(new Move("resign"));
        return true;
    }





}



