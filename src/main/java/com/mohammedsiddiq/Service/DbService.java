package com.mohammedsiddiq.Service;

import com.mohammedsiddiq.DTOs.MakeMove;
import com.mohammedsiddiq.DTOs.Move;
import com.mohammedsiddiq.DTOs.StartGameResponse;
import com.mohammedsiddiq.DbObjects.GameDbo;
import com.mohammedsiddiq.EngineInterface.Session;
import com.mohammedsiddiq.Repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DbService {
    @Autowired
    GameRepository gameRepository;

    public GameDbo addNewGame(StartGameResponse startGameResponse) {
        GameDbo newGameDbo = new GameDbo();
        newGameDbo.setGameId(startGameResponse.getSession().getSessionId());

        // If first move wasn't set then the requesting user plays white
        if (startGameResponse.getFirstMove() == null) {
            newGameDbo.setWhitePlayer(startGameResponse.getSession().getUserName());
            newGameDbo.setBlackPlayer(startGameResponse.getYourOpponentName());
        } else {
            newGameDbo.setBlackPlayer(startGameResponse.getSession().getUserName());
            newGameDbo.setWhitePlayer(startGameResponse.getYourOpponentName());
            newGameDbo.updateMove(startGameResponse.getFirstMove().getMyMove());

        }
        newGameDbo.setActive(true); //Active game
        newGameDbo.setResult("Active");
        System.out.println(gameRepository.findAll());
        return gameRepository.save(newGameDbo);

    }

    public GameDbo updateMove(MakeMove restBasedOpponentMove, Move computerMove) {

        GameDbo existingGameDbo = gameRepository.findById(restBasedOpponentMove.getGameId()).get();
        existingGameDbo.updateMove(restBasedOpponentMove.getMove().getMyMove());
        existingGameDbo.updateMove(computerMove.getMyMove());
        if (computerMove.getGameStatus().equals("Alive")) {
            existingGameDbo.setActive(true);
        }
        existingGameDbo.setResult(computerMove.getGameStatus());

        return gameRepository.save(existingGameDbo);
    }

    public GameDbo getGameState(int gameId) {
        return gameRepository.findById(gameId).orElse(new GameDbo());
    }

    public GameDbo updateOnQuit(Session session) {
        GameDbo existingGameDbo = gameRepository.findById(session.getSessionId()).get();
        existingGameDbo.setActive(false);
        existingGameDbo.setResult(session.getUserName() + "-Quit!");
        return gameRepository.save(existingGameDbo);


    }
}
