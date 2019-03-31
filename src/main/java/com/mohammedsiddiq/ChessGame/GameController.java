package com.mohammedsiddiq.ChessGame;

import com.mohammedsiddiq.DTOs.MakeMove;
import com.mohammedsiddiq.DTOs.Move;
import com.mohammedsiddiq.DTOs.Response;
import com.mohammedsiddiq.DTOs.StartGameResponse;
import com.mohammedsiddiq.DbObjects.GameDbo;
import com.mohammedsiddiq.EngineInterface.Session;
import com.mohammedsiddiq.Service.DbService;
import com.mohammedsiddiq.Service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("Chess")
public class GameController {


    @Autowired
    GameService gameService;

    @Autowired
    DbService dbService;


    @GetMapping("/newGame")
    public StartGameResponse newGame(String name, boolean firstMove) {

        StartGameResponse starGameResponse = gameService.newGame(name, firstMove);
        //Updating the DB state
        dbService.addNewGame(starGameResponse);
        return starGameResponse;
    }

    @PostMapping("/Move")
    public Move makeMove(@RequestBody MakeMove move) {


        Move myMove = gameService.move(move.getMove(), move.getGameId());
        dbService.updateMove(move, myMove);

        return myMove;

    }

    @PostMapping("/quitGame")
    public Response quitGame(@RequestBody Session session) {

        Response response = gameService.quit(session);

        dbService.updateOnQuit(session);
        return response;
    }

    @GetMapping("/gameState")
    public GameDbo gameState(int gameId) {

        return dbService.getGameState(gameId);
    }






}