package com.mohammedsiddiq.ChessGame.RestClient;

import com.mohammedsiddiq.ChessGame.DTOs.MakeMove;
import com.mohammedsiddiq.ChessGame.DTOs.Move;
import com.mohammedsiddiq.ChessGame.DTOs.StartGameResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GameClient {
    @GET("/Chess/newGame")
    Call<StartGameResponse> startNewGame(String userName, boolean firstMove);

    @POST("/Chess/makeMove")
    Call<Move> makeNextMove(@Body MakeMove move);

}
