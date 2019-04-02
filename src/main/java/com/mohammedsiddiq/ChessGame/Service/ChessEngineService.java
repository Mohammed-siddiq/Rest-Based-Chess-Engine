package com.mohammedsiddiq.ChessGame.Service;

import org.petero.cuckoo.engine.chess.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mohammedsiddiq.ChessGame.DTOs.Move;

import java.io.IOException;


public class ChessEngineService extends Game {


    Logger logger = LoggerFactory.getLogger(this.getClass());

    public ChessEngineService(Player whitePlayer, Player blackPlayer) {
        super(whitePlayer, blackPlayer);
        handleCommand("new");
    }

    @Override
    protected boolean handleCommand(String moveStr) {
        if (super.handleCommand(moveStr))
            return true;
        return false;
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

    public void play() throws IOException {

        // Print last move
//            if (currentMove > 0) {
//                Position prevPos = new Position(pos);
//                prevPos.unMakeMove(moveList.get(currentMove - 1), uiInfoList.get(currentMove - 1));
//                String moveStr= TextIO.moveToString(prevPos, moveList.get(currentMove - 1), false);
//                if (haveDrawOffer()) {
//                    moveStr += " (offer draw)";
//                }
//                String msg = String.format("Last move: %d%s %s",
//                        prevPos.fullMoveCounter, prevPos.whiteMove ? "." : "...",
//                        moveStr);
//                System.out.println(msg);
//            }
//            System.out.printf("Hash: %016x\n", pos.zobristHash());
//        {
//            Evaluate eval = new Evaluate();
//            int evScore = eval.evalPos(pos) * (pos.whiteMove ? 1 : -1);
//            System.out.printf("Eval: %.2f%n", evScore / 100.0);
//        }

        // Check game state
//        System.out.print(TextIO.asciiBoard(pos));
        String stateStr = getGameStateString();
        if (stateStr.length() > 0) {
            System.out.printf("Printing game state: %s%n", stateStr);
        }
        if (getGameState() != GameState.ALIVE) {
            activateHumanPlayer();
        }

        // Get command from current player and act on it
        Player pl = pos.whiteMove ? whitePlayer : blackPlayer;
        String moveStr = pl.getCommand(new Position(pos), haveDrawOffer(), getHistory());
        if (moveStr.equals("quit")) {
            return;
        } else {
            boolean ok = processString(moveStr);
            if (!ok) {
                System.out.printf("Invalid move: %s\n", moveStr);
            }
        }
    }

    public void printChessBoard() {
        System.out.print(TextIO.asciiBoard(pos));
    }

    Move makeOpponentsNextMove(Move move) {

        //Get the opponent player
        Player pl = pos.whiteMove ? whitePlayer : blackPlayer;

        Move myMove = new Move();
        if (getCurrentGameState() != GameState.ALIVE) {
            logger.debug("Game is not alive");
            myMove.setMessage(getGameStateString());
            myMove.setMyMove("-");
            return myMove;
        }

        RestBasedOpponentPlayer restBasedOpponentPlayer = (RestBasedOpponentPlayer) pl;

        //set the next move for the Rest Based player under the custom opponent class through the next move
        restBasedOpponentPlayer.setMyNextMove(move.getMyMove());

        myMove.setMyMove(getPlayersCommand(restBasedOpponentPlayer));
        myMove.setMessage("Your Turn!");
        logger.info("Returning my move {}", myMove.getMyMove());
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

            logger.debug("Game is not alive");
            myMove.setMessage(getGameStateString());
            myMove.setMyMove("-");
            return myMove;
        }

        Player pl = pos.whiteMove ? whitePlayer : blackPlayer;


        String moveStr = getPlayersCommand(pl);


        myMove.setMyMove(moveStr);
        myMove.setMessage("Your Turn!");
        myMove.setPlayer("White (Alpha)");
        logger.info("Returning my move {}", myMove.getMyMove());
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



