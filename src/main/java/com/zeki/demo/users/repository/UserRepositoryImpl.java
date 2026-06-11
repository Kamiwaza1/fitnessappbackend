package com.zeki.demo.users.repository;

import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.mapperDomainEntity.UserDomainEntity;
import com.zeki.demo.users.repository.jpa.UserRepoJPA;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserRepoJPA userRepoJPA;
    private final UserDomainEntity userDomainEntity;

    @Override
    public Optional<User> findByEmail(String normalizedEmail) {
        return userRepoJPA.findByEmailIgnoreCase(normalizedEmail).map(userDomainEntity::toDomain);
    }

    @Override
    public Optional<User> findActiveByEmail(String normalizedEmail) {
        return userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue(normalizedEmail).map(userDomainEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String normalizedEmail) {
        return userRepoJPA.existsByEmailIgnoreCase(normalizedEmail);
    }

    @Override
    public boolean existsActiveByEmail(String normalizedEmail) {
        return userRepoJPA.existsByEmailIgnoreCaseAndIsActiveTrue(normalizedEmail);
    }

    @Override
    public User save(User user) {
        return userDomainEntity.toDomain(userRepoJPA.save(userDomainEntity.toEntity(user)));
    }

    @Override
    public List<User> findUsersReadyForHardDelete(LocalDateTime now) {
        return userRepoJPA.findAllByDeletionScheduledForLessThanEqual(now)
                .stream()
                .map(userDomainEntity::toDomain)
                .toList();
    }

    @Override
    public void hardDeleteById(UUID userId, String email) {
        userRepoJPA.deleteLiveWorkoutParticipantsByEmail(email);
        userRepoJPA.deleteWorkoutHistoryByUserEmail(email);
        userRepoJPA.deleteNutritionEntriesByUserId(userId);
        userRepoJPA.deleteWorkoutsByUserId(userId);
        userRepoJPA.deleteWorkoutHistoryByUserEmail(email);
        userRepoJPA.deleteById(userId);
    }
}
