package com.mohammedsiddiq.RestClient;

import com.mohammedsiddiq.DTOs.MakeMove;
import com.mohammedsiddiq.DTOs.Move;
import com.mohammedsiddiq.DTOs.StartGameResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GameClient {

    @GET("/Chess/newGame")
    Call<StartGameResponse> startNewGame(@Query("userName") String userName, @Query("firstMove") boolean firstMove);

    @POST("/Chess/Move")
    Call<Move> makeNextMove(@Body MakeMove move);

}
