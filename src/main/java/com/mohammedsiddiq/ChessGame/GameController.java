package com.mohammedsiddiq.ChessGame.ChessGame;

import java.util.concurrent.atomic.AtomicLong;

import com.mohammedsiddiq.ChessGame.DTOs.MakeMove;
import com.mohammedsiddiq.ChessGame.DTOs.Move;
import com.mohammedsiddiq.ChessGame.DTOs.Response;
import com.mohammedsiddiq.ChessGame.DTOs.StartGameResponse;
import com.mohammedsiddiq.ChessGame.EngineInterface.Session;
import com.mohammedsiddiq.ChessGame.Service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("Chess")
public class GameController {


    @Autowired
    GameService gameService;

    @GetMapping("/newGame")
    public StartGameResponse newGame(String name, boolean firstMove) {

        return gameService.newGame(name, firstMove);
    }

    @PostMapping("/Move")
    public Move makeMove(@RequestBody MakeMove move) {


        Move myMove = gameService.move(move.getMove(), move.getGameId());

        return myMove;

    }

    @GetMapping("/quitGame")
    public Response quitGame(Session session) {

        return gameService.quit(session);
    }


}