package io.github.iamkrishna73.jwtauthenticationservice.repository;

import io.github.iamkrishna73.jwtauthenticationservice.entity.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends MongoRepository<AppUser, String> {
    boolean existsByUsername(String username);
    Optional<AppUser> findByUsername(String username);
}
