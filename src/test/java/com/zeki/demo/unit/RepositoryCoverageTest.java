package com.zeki.demo.Unit;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import com.zeki.demo.WorkoutHistory.entity.WorkoutHistoryEntity;
import com.zeki.demo.WorkoutHistory.mapperEntityDomain.WorkoutHistoryEntityMapper;
import com.zeki.demo.WorkoutHistory.repository.WorkoutHistoryRepositoryImpl;
import com.zeki.demo.WorkoutHistory.repository.jpa.WorkoutHistoryRepoJPA;
import com.zeki.demo.common.exception.NotFoundException;
import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.entity.NutritionEntity;
import com.zeki.demo.nutrition.mapperEntityDomain.NutritionEntityMapper;
import com.zeki.demo.nutrition.repository.NutritionRepositoryImpl;
import com.zeki.demo.nutrition.repository.jpa.NutritionRepoJPA;
import com.zeki.demo.progress.repository.ProgressRepositoryImpl;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.entity.UserEntity;
import com.zeki.demo.users.mapperDomainEntity.UserDomainEntity;
import com.zeki.demo.users.repository.UserRepositoryImpl;
import com.zeki.demo.users.repository.jpa.UserRepoJPA;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.entity.WorkoutEntity;
import com.zeki.demo.workouts.mapperEntityDomain.WorkoutEntityMapper;
import com.zeki.demo.workouts.repository.WorkoutRepositoryImpl;
import com.zeki.demo.workouts.repository.jpa.WorkoutRepoJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RepositoryCoverageTest {

    private static final String EMAIL = "owner@example.com";

    private UUID id;
    private UserEntity userEntity;

    private WorkoutRepoJPA workoutRepoJPA;
    private WorkoutEntityMapper workoutMapper;
    private WorkoutRepositoryImpl workoutRepository;

    private NutritionRepoJPA nutritionRepoJPA;
    private NutritionEntityMapper nutritionMapper;
    private NutritionRepositoryImpl nutritionRepository;

    private UserRepoJPA userRepoJPA;
    private UserDomainEntity userMapper;
    private UserRepositoryImpl userRepository;

    private WorkoutHistoryRepoJPA historyRepoJPA;
    private WorkoutHistoryEntityMapper historyMapper;
    private WorkoutHistoryRepositoryImpl historyRepository;

    private ProgressRepositoryImpl progressRepository;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        userEntity = new UserEntity();

        userRepoJPA = mock(UserRepoJPA.class);

        workoutRepoJPA = mock(WorkoutRepoJPA.class);
        workoutMapper = mock(WorkoutEntityMapper.class);
        workoutRepository = new WorkoutRepositoryImpl(workoutRepoJPA, workoutMapper, userRepoJPA);

        nutritionRepoJPA = mock(NutritionRepoJPA.class);
        nutritionMapper = mock(NutritionEntityMapper.class);
        nutritionRepository = new NutritionRepositoryImpl(nutritionRepoJPA, nutritionMapper, userRepoJPA);

        userMapper = mock(UserDomainEntity.class);
        userRepository = new UserRepositoryImpl(userRepoJPA, userMapper);

        historyRepoJPA = mock(WorkoutHistoryRepoJPA.class);
        historyMapper = mock(WorkoutHistoryEntityMapper.class);
        historyRepository = new WorkoutHistoryRepositoryImpl(historyRepoJPA, historyMapper);

        progressRepository = new ProgressRepositoryImpl(workoutRepoJPA, workoutMapper);
    }

    @Test
    void workoutRepositoryMapsFindSaveDeleteAndUpcomingOperations() {
        Workout workout = mock(Workout.class);
        WorkoutEntity entity = new WorkoutEntity();
        stubWorkoutSave(workout, entity);
        when(workoutRepoJPA.findByWorkoutIdAndUserEmail(id, EMAIL)).thenReturn(Optional.of(entity));
        when(workoutRepoJPA.findAllByUserEmailOrderByWorkoutDateDescWorkoutIdDesc(EMAIL)).thenReturn(List.of(entity));
        when(workoutRepoJPA.findById(id)).thenReturn(Optional.of(entity));
        when(workoutRepoJPA.findAllByUserEmailAndWorkoutDateGreaterThanEqualOrderByWorkoutDateAscWorkoutIdAsc(any(), any()))
                .thenReturn(List.of(entity));

        assertEquals(workout, workoutRepository.save(workout));
        assertEquals(Optional.of(workout), workoutRepository.getWorkoutByIdAndUserEmail(id, EMAIL));
        assertEquals(List.of(workout), workoutRepository.findAllByUserEmail(EMAIL));
        assertEquals(List.of(workout), workoutRepository.upcomingWorkouts(EMAIL));
        workoutRepository.delete(id);

        verify(workoutRepoJPA).delete(entity);
    }

    @Test
    void repositoriesThrowNotFoundWhenRequiredRowsAreMissing() {
        Workout workout = mock(Workout.class);
        Nutrition nutrition = mock(Nutrition.class);
        when(workout.getUserEmail()).thenReturn(EMAIL);
        when(nutrition.getUserEmail()).thenReturn(EMAIL);
        when(userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue(EMAIL)).thenReturn(Optional.empty());

        assertInstanceOf(NotFoundException.class, assertThrows(RuntimeException.class, () -> workoutRepository.save(workout)));
        assertInstanceOf(NotFoundException.class, assertThrows(RuntimeException.class, () -> nutritionRepository.save(nutrition)));
    }

    @Test
    void nutritionRepositoryMapsFindSaveAndDeleteOperations() {
        Nutrition nutrition = mock(Nutrition.class);
        NutritionEntity entity = new NutritionEntity();
        stubNutritionSave(nutrition, entity);
        when(nutritionRepoJPA.findByNutritionIdAndUserEmail(id, EMAIL)).thenReturn(Optional.of(entity));
        when(nutritionRepoJPA.findAllByUserEmailOrderByMealDateDescNutritionIdDesc(EMAIL)).thenReturn(List.of(entity));
        when(nutritionRepoJPA.findById(id)).thenReturn(Optional.of(entity));

        assertEquals(nutrition, nutritionRepository.save(nutrition));
        assertEquals(Optional.of(nutrition), nutritionRepository.getNutritionByIdAndUserEmail(id, EMAIL));
        assertEquals(List.of(nutrition), nutritionRepository.findAllByUserEmail(EMAIL));
        nutritionRepository.delete(id);

        verify(nutritionRepoJPA).delete(entity);
    }

    @Test
    void userRepositoryMapsJpaResults() {
        User user = mock(User.class);
        when(userRepoJPA.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(userEntity));
        when(userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue(EMAIL)).thenReturn(Optional.of(userEntity));
        when(userRepoJPA.existsByEmailIgnoreCase(EMAIL)).thenReturn(true);
        when(userRepoJPA.existsByEmailIgnoreCaseAndIsActiveTrue(EMAIL)).thenReturn(true);
        when(userMapper.toDomain(userEntity)).thenReturn(user);
        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(userRepoJPA.save(userEntity)).thenReturn(userEntity);

        assertEquals(Optional.of(user), userRepository.findByEmail(EMAIL));
        assertEquals(Optional.of(user), userRepository.findActiveByEmail(EMAIL));
        assertTrue(userRepository.existsByEmail(EMAIL));
        assertTrue(userRepository.existsActiveByEmail(EMAIL));
        assertEquals(user, userRepository.save(user));
    }

    @Test
    void historyAndProgressRepositoriesMapJpaResults() {
        WorkoutHistoryEntity historyEntity = new WorkoutHistoryEntity();
        WorkoutHistory history = mock(WorkoutHistory.class);
        WorkoutEntity workoutEntity = new WorkoutEntity();
        Workout workout = mock(Workout.class);

        when(historyRepoJPA.findAllByUserEmailOrderByWorkoutDateDescWorkoutIdDesc(EMAIL)).thenReturn(List.of(historyEntity));
        when(historyRepoJPA.findByWorkoutIdAndUserEmail(id, EMAIL)).thenReturn(Optional.of(historyEntity));
        when(historyMapper.toDomain(historyEntity)).thenReturn(history);
        when(workoutRepoJPA.findAllByUserEmailOrderByWorkoutDateDescWorkoutIdDesc(EMAIL)).thenReturn(List.of(workoutEntity));
        when(workoutMapper.toDomain(workoutEntity)).thenReturn(workout);

        assertEquals(List.of(history), historyRepository.findAllByUserEmail(EMAIL));
        assertEquals(Optional.of(history), historyRepository.getByWorkoutIdAndUserEmail(id, EMAIL));
        assertEquals(List.of(workout), progressRepository.findAllByUserEmail(EMAIL));
    }

    private void stubWorkoutSave(Workout workout, WorkoutEntity entity) {
        when(workout.getUserEmail()).thenReturn(EMAIL);
        when(userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue(EMAIL)).thenReturn(Optional.of(userEntity));
        when(workoutMapper.toEntity(workout, userEntity)).thenReturn(entity);
        when(workoutRepoJPA.save(entity)).thenReturn(entity);
        when(workoutMapper.toDomain(entity)).thenReturn(workout);
    }

    private void stubNutritionSave(Nutrition nutrition, NutritionEntity entity) {
        when(nutrition.getUserEmail()).thenReturn(EMAIL);
        when(userRepoJPA.findByEmailIgnoreCaseAndIsActiveTrue(EMAIL)).thenReturn(Optional.of(userEntity));
        when(nutritionMapper.toEntity(nutrition, userEntity)).thenReturn(entity);
        when(nutritionRepoJPA.save(entity)).thenReturn(entity);
        when(nutritionMapper.toDomain(entity)).thenReturn(nutrition);
    }
}
