# Overview


Exposes a chess game via REST calls. Also includes auto-play feature - where an instance(or multiple instances) can play chess game/games with other instances. Multiple games and their states are stored in the MongoDB.

- Uses an open source chess engine called [cuckoo-chess-engine](http://hem.bredband.net/petero2b/javachess/index.html)
- Uses the following interface to interact with the chess engine and the rest calls.

```java
public interface IChessEngine {

    /**
     * Creates a new Game
     * @param userName The player name
     * @param firstMove if true the opponent makes the first move
     * @return startgameResponse obj
     */
    StartGameResponse newGame(String userName, boolean firstMove);

    /**
     * Performs a move on the chess engine for the corresponding session and returns the opponent's move
     * @param move Move to be done
     * @param sessionId which sessions move
     * @return opponent's move
     */
    Move move(Move move, int sessionId);


    /**
     * Quits the game
     * @param sessionId which session to quit
     * @return Success/failure after quitting
     */
    Response quit(Session sessionId);
} 
```

where StartGameResponse,Move and Sessions are the Data Transfer Objects (DTO) Refer the `package com.mohammedsiddiq.DTOs`.

- Supports multiple games through sessions - On the request of a new game a unique sessionID/gameId is generated which must be passed used by the client to make a move to a corresponding game.

- The state of every game is stored in MongoDB, so that the game state can be retrieved anytime and restored in the future.

Game APIs
-

- Create a new Game - **GET** `Chess/newGame?name=playerName&firstMove=true`

    where `playerName` is the name of the player and `FirstMove` describes who takes the first move.
       
     Example:
     
**Request** : 

    
    curl -X GET \
      'http://127.0.0.1:8080/Chess/newGame?name=Mohammed%20Siddiq&firstMove=true' \
      -H 'Postman-Token: 3eed55ed-04e4-4138-83bf-6729eb239583' \
      -H 'cache-control: no-cache'       

**Response** :
    
    {
        "session": {
            "sessionId": 1342174387,
            "userName": "Mohammed Siddiq"
        },
        "yourOpponentName": "Alpha",
        "firstMove": {
            "myMove": "c4",
            "player": "white - Alpha",
            "message": "Your Turn!",
            "gameStatus": "Alive"
        }
    }
    


- Make Move -  **POST** `/Chess/Move`

 **Request** :  
 

    curl -X POST \
         http://localhost:8080/Chess/Move \
         -H 'Content-Type: application/json' \
         -H 'Postman-Token: 0edfda18-9bb4-46ac-a470-0e33f7d7ab42' \
         -H 'cache-control: no-cache' \
         -d '{
        "gameId":1342174387,
        "move":{ 
            "myMove":"Nc6"
        }
       
       }



This request takes in the gameId provided as the response to the newGame request and the move to be made fort he rest based user.

The response is the move made by the server in response to the move made by the client.

**Response** :


    
    {
        "myMove": "Nc3",
        "player": "white - Alpha",
        "message": "Your Turn!",
        "gameStatus": "Alive"
    }
    

 
 - Get the game state - **GET** - `/Chess/gameState?gameId=1342174387`
 
 **Request** : 
 
    
     curl -X GET \
      'http://localhost:8080/Chess/gameState?gameId=1342174387' \
      -H 'Postman-Token: b65ed466-24c6-4c0a-a48c-34654df9c85e' \
      -H 'cache-control: no-cache'
    

**Response** :


  
    {
        "gameId": 1342174387,
        "whitePlayer": "Alpha",
        "blackPlayer": "Mohammed Siddiq",
        "result": "Alive",
        "moves": [
            "c4",
            "c5",
            "Nf3",
            "Nc6",
            "Nc3"
        ],
        "active": true,
        "firstMove": false
    }


- Quit Game : **POST**  : `8080/Chess/quitGame`

**Request**


    curl -X POST \
      'http://localhost:8080/Chess/quitGame \
      -H 'Content-Type: application/json' \
      -H 'Postman-Token: 31c3a148-5d0f-4263-bf65-faf020725714' \
      -H 'cache-control: no-cache' \
      -d '{
            "sessionId": 1968087944,
            "userName": "Mohammed Siddiq"
    }'
    


**Response**


    {
        "message": "Successfully terminated the game for the opponent Mohammed Siddiq"
    }



_Note on the Game state stored in the mongoDB_
-

The following is a document stored in the collection of gameDBO in MongoDb.




    
    {
      "_id": 625850701,
      "whitePlayer": "Alpha",
      "result": "Game over, black mates!",
      "moves": [
        "e4",
        "Nf6",
        "e5",
        "Nd5",
        "d4",
        "d6",
        "Nf3",
        "Bg4",
        "Be2",
        "e6",
        "c4",
        "Nb6",
        "exd6",
        "cxd6",
        "O-O",
        "Nc6",
        "Bg5",
        "Be7",
        "Bxe7",
        "Qxe7",
        "Nbd2",
        "O-O",
        "Qc2",
        "Nb4",
        "Qe4",
        "Bf5",
        "Qf4",
        "Rac8",
        "a3",
        "Nd3",
        "Bxd3",
        "Bxd3",
        "Rfc1",
        "Nxc4",
        "Qe3",
        "Nxe3",
        "Rab1",
        "Bxb1",
        "Rxb1",
        "Nd5",
        "Ne4",
        "Rc2",
        "Ne1",
        "Re2",
        "f3",
        "Rc8",
        "Nd3",
        "Rcc2",
        "Nef2",
        "Qg5",
        "Rd1",
        "Red2",
        "Ra1",
        "Rxd3",
        "f4",
        "Qxf4",
        "Nxd3",
        "Qxd4+",
        "Kh1",
        "Qxd3",
        "Rb1",
        "Rxg2",
        "Rc1",
        "Rxh2+",
        "Kxh2",
        "Qd2+",
        "Kg3",
        "Qxc1",
        "b3",
        "Qg1+",
        "Kf3",
        "Qe3+",
        "Kg2",
        "Nf4+",
        "Kf1",
        "Qe2+",
        "Kg1",
        "Qg2#",
        "-"
      ],
      "active": false,
      "firstMove": false,
      "_class": "com.mohammedsiddiq.DbObjects.GameDbo"
    }

 Docker and Capstan build
-

- Docker file is a simple straightforward specification and most of the cmdline arguments would be overridden while running the docker image.

dockerfile

```dockerfile
FROM java:8

WORKDIR /

ADD RestBasedChessEngine-1.0-SNAPSHOT.jar app.jar

CMD java -jar app.jar --server.port=8090 false
```
Docker BUILD

    docker build -t chess-engine .

 make sure the working directory has the jar built using 
 
    mvn clean package
  
  If all the test cases pass (which they will) , a jar would be created in the target folder by the name `RestBasedChessEngine-1.0-SNAPSHOT.jar`

You can directly pull the docker image and run

    docker pull immohammedsiddiq/rest-chess-engine-ai
`
Run a single instance first without autoplay on :

    docker run -p 8090:8090 immohammedsiddiq/rest-chess-engine-ai java -jar app.jar --server.port=8090 false --spring.data.mongodb.host=172.17.0.2
    
    
where `172.17.0.2` is the mongo host. You can run a docker image of mongo host and provide its IP

Run mongo using

    docker run mongo
    
In order to let two instances play against each other run the following command

     docker run -p 8010:8010 immohammedsiddiq/rest-chess-engine-ai java -jar app.jar --server.port=8010 true \[**otherInstanceIP**]:\[**OtherInstancePort**] --spring.data.mongodb.host=172.17.0.2
     
Add the IP and port of other running instance in the above command/
    
- **Capstan build file:**

```
base: cloudius/osv-openjdk8
files:
 /app.jar: ./RestBasedChessEngine-1.0-SNAPSHOT.jar
cmdline: java -jar /app.jar --server.port=8080 false
```

Make sure capstan installed on your machine and do 

    capstan build

You can plugin a docker image and run the capstan build :

    sudo docker run -it -v /capstan-repository:/capstan-repository cloudrouter/osv-builder capstan run
    
    
Ensure that /capstan-repostiory on your local file system has the capstan built file

Form demo and running on amazon AWS, watch this video :
[![Refer this youtube Video](https://github.com/Mohammed-siddiq/Rest-Based-Chess-Engine/blob/master/ScreenShots/Screen%20Shot%202019-03-31%20at%203.44.54%20PM.png)](https://youtu.be/_COcH8cPyps "Demo of the project" )

##### Snapshots from the AWS cloud and running instances

![AWS console with isntance provisioned](https://github.com/Mohammed-siddiq/Rest-Based-Chess-Engine/blob/master/ScreenShots/Screen%20Shot%202019-03-31%20at%206.42.34%20PM.png)

![AWS running instance container logs](https://drive.google.com/file/d/11k37jRnoTfOMq6GqM_ZUTkAmbP_NuVBc/view?usp=sharing)

![AWS container status](https://github.com/Mohammed-siddiq/Rest-Based-Chess-Engine/blob/master/ScreenShots/Screen%20Shot%202019-03-31%20at%206.42.44%20PM.png)











