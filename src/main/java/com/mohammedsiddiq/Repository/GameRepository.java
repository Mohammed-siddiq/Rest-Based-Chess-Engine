package com.mohammedsiddiq.Repository;

import com.mohammedsiddiq.DbObjects.GameDbo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<GameDbo, Integer> {
}
