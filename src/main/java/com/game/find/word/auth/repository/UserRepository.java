package com.game.find.word.auth.repository;

import com.game.find.word.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User>  findByProviderIdAndProvider(String providerId, String provider);

}