package com.mohammedsiddiq;

import com.mohammedsiddiq.DTOs.MakeMove;
import com.mohammedsiddiq.DTOs.Move;
import com.mohammedsiddiq.DTOs.Response;
import com.mohammedsiddiq.DTOs.StartGameResponse;
import com.mohammedsiddiq.Service.GameService;
import org.junit.Assert;
import org.junit.Test;

public class TestGameService {

    GameService gameService = new GameService();
    String userName = "Test user";


    @Test
    public void verifyNewGameCreationAsAWhitePlayer()
    {
        StartGameResponse response = gameService.newGame(userName, false);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getSession().getUserName().equals(userName));
        Assert.assertTrue(response.getSession().getSessionId()!=0);

        //Since test user is white the first move must not be returned
        Assert.assertNull(response.getFirstMove());
    }

    @Test
    public void verifyNewGameCreationAsABlackPlayer()
    {

        StartGameResponse response = gameService.newGame(userName, true);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.getSession().getUserName().equals(userName));
        Assert.assertTrue(response.getSession().getSessionId()!=0);

        //Since test user is white the first move must not be returned
        Assert.assertNotNull(response.getFirstMove() );
    }

    @Test
    public void verifyQuitGame()
    {

        StartGameResponse response = gameService.newGame(userName, true);

        Assert.assertTrue(response!=null);
        Assert.assertTrue(response.getSession().getUserName().equals(userName));
        Assert.assertTrue(response.getSession().getSessionId()!=0);

        Response quitResponse = gameService.quit(response.getSession());
        Assert.assertNotNull(quitResponse.getMessage());


    }

    @Test
    public void verifyAutoPlayMove()
    {
        StartGameResponse response = gameService.newGame(userName, false);

        Assert.assertTrue(response!=null);
        Assert.assertTrue(response.getSession().getUserName().equals(userName));
        Assert.assertTrue(response.getSession().getSessionId()!=0);

        //Making a autoplay's opponent move
        Move move = new Move("e4");
        Move responseMove = gameService.move(move, response.getSession().getSessionId());

        //response Move must be populated
        Assert.assertNotNull(responseMove);
        Assert.assertNotNull(responseMove.getMyMove());


    }
}
