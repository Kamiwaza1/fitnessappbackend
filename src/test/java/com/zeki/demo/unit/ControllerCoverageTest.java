package com.zeki.demo.Unit;

import com.zeki.demo.WorkoutHistory.domain.WorkoutHistory;
import com.zeki.demo.WorkoutHistory.dto.WorkoutHistoryResponse;
import com.zeki.demo.WorkoutHistory.mapperDtoDomain.WorkoutHistoryDtoDomain;
import com.zeki.demo.WorkoutHistory.service.WorkoutHistoryService;
import com.zeki.demo.exerciseLibrary.controller.ExerciseLibraryController;
import com.zeki.demo.exerciseLibrary.domain.ExerciseLibrary;
import com.zeki.demo.exerciseLibrary.domain.ExerciseLibraryItem;
import com.zeki.demo.exerciseLibrary.domain.ExerciseLibraryListCriteria;
import com.zeki.demo.exerciseLibrary.domain.ExerciseLibrarySearchCriteria;
import com.zeki.demo.exerciseLibrary.domain.ExerciseMuscle;
import com.zeki.demo.exerciseLibrary.domain.ExerciseReference;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryItemResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryListRequest;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibraryResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseLibrarySearchRequest;
import com.zeki.demo.exerciseLibrary.dto.ExerciseMuscleResponse;
import com.zeki.demo.exerciseLibrary.dto.ExerciseReferenceResponse;
import com.zeki.demo.exerciseLibrary.mapperDtoDomain.ExerciseLibraryDtoDomain;
import com.zeki.demo.exerciseLibrary.service.ExerciseLibraryService;
import com.zeki.demo.liveworkouts.controller.LiveWorkoutRoomController;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutMessage;
import com.zeki.demo.liveworkouts.domain.LiveWorkoutRoom;
import com.zeki.demo.liveworkouts.dto.CreateLiveWorkoutRoomRequest;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutMessageRequest;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutMessageResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutProgressRequest;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutRoomDetailResponse;
import com.zeki.demo.liveworkouts.dto.LiveWorkoutRoomResponse;
import com.zeki.demo.liveworkouts.mapperDtoDomain.LiveWorkoutMessageMapper;
import com.zeki.demo.liveworkouts.mapperDtoDomain.LiveWorkoutRoomMapper;
import com.zeki.demo.liveworkouts.service.LiveWorkoutEventPublisher;
import com.zeki.demo.liveworkouts.service.LiveWorkoutMessageService;
import com.zeki.demo.liveworkouts.service.LiveWorkoutRoomService;
import com.zeki.demo.nutrition.controller.NutritionController;
import com.zeki.demo.nutrition.domain.Nutrition;
import com.zeki.demo.nutrition.dto.CreateNutritionRequest;
import com.zeki.demo.nutrition.dto.FoodProductResponse;
import com.zeki.demo.nutrition.dto.FoodProductSearchRequest;
import com.zeki.demo.nutrition.dto.FoodProductsRequest;
import com.zeki.demo.nutrition.dto.FoodProductsResponse;
import com.zeki.demo.nutrition.dto.NutritionResponse;
import com.zeki.demo.nutrition.dto.UpdateNutritionRequest;
import com.zeki.demo.nutrition.enums.NutritionCategory;
import com.zeki.demo.nutrition.mapperDtoDomain.NutritionDtoDomain;
import com.zeki.demo.nutrition.service.NutritionService;
import com.zeki.demo.progress.controller.ProgressController;
import com.zeki.demo.progress.domain.Progress;
import com.zeki.demo.progress.dto.ProgressResponse;
import com.zeki.demo.progress.mapperDtoDomain.ProgressDtoDomain;
import com.zeki.demo.progress.service.ProgressService;
import com.zeki.demo.users.controller.AuthController;
import com.zeki.demo.users.controller.UserController;
import com.zeki.demo.users.domain.User;
import com.zeki.demo.users.dto.AccountDeletionRequest;
import com.zeki.demo.users.dto.AuthResponse;
import com.zeki.demo.users.dto.LoginRequest;
import com.zeki.demo.users.dto.RefreshTokenRequest;
import com.zeki.demo.users.dto.RegisterRequest;
import com.zeki.demo.users.dto.UpdateUserRequest;
import com.zeki.demo.users.dto.UserResponse;
import com.zeki.demo.users.mapperDtoDomain.UserDtoDomain;
import com.zeki.demo.users.security.JwtCookieService;
import com.zeki.demo.users.service.AuthService;
import com.zeki.demo.users.service.AuthenticatedUser;
import com.zeki.demo.users.service.UserService;
import com.zeki.demo.workouts.controller.WorkoutController;
import com.zeki.demo.workouts.domain.Workout;
import com.zeki.demo.workouts.dto.CreateWorkoutRequest;
import com.zeki.demo.workouts.dto.UpdateWorkoutRequest;
import com.zeki.demo.workouts.dto.WorkoutResponse;
import com.zeki.demo.workouts.enums.MuscleType;
import com.zeki.demo.workouts.enums.WorkoutCategory;
import com.zeki.demo.workouts.mapperDtoDomain.WorkoutDtoDomain;
import com.zeki.demo.workouts.service.WorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControllerCoverageTest {

    private static final String EMAIL = "owner@example.com";

    private UUID id;
    private Authentication auth;

    private WorkoutService workoutService;
    private WorkoutDtoDomain workoutMapper;
    private WorkoutController workoutController;

    private NutritionService nutritionService;
    private NutritionDtoDomain nutritionMapper;
    private NutritionController nutritionController;

    private LiveWorkoutRoomService liveWorkoutService;
    private LiveWorkoutMessageService liveWorkoutMessageService;
    private LiveWorkoutRoomMapper liveWorkoutMapper;
    private LiveWorkoutMessageMapper liveWorkoutMessageMapper;
    private LiveWorkoutEventPublisher liveWorkoutEventPublisher;
    private LiveWorkoutRoomController liveWorkoutController;

    private ExerciseLibraryService exerciseService;
    private ExerciseLibraryDtoDomain exerciseMapper;
    private ExerciseLibraryController exerciseController;

    private UserService userService;
    private AuthService authService;
    private JwtCookieService jwtCookieService;
    private UserDtoDomain userMapper;
    private UserController userController;
    private AuthController authController;

    private ProgressService progressService;
    private ProgressDtoDomain progressMapper;
    private ProgressController progressController;

    private WorkoutHistoryService historyService;
    private WorkoutHistoryDtoDomain historyMapper;
    private com.zeki.demo.WorkoutHistory.controller.WorkoutHistoryController historyController;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        auth = authentication();

        workoutService = mock(WorkoutService.class);
        workoutMapper = mock(WorkoutDtoDomain.class);
        workoutController = new WorkoutController(workoutService, workoutMapper);

        nutritionService = mock(NutritionService.class);
        nutritionMapper = mock(NutritionDtoDomain.class);
        nutritionController = new NutritionController(nutritionService, nutritionMapper);

        liveWorkoutService = mock(LiveWorkoutRoomService.class);
        liveWorkoutMessageService = mock(LiveWorkoutMessageService.class);
        liveWorkoutMapper = mock(LiveWorkoutRoomMapper.class);
        liveWorkoutMessageMapper = mock(LiveWorkoutMessageMapper.class);
        liveWorkoutEventPublisher = mock(LiveWorkoutEventPublisher.class);
        liveWorkoutController = new LiveWorkoutRoomController(
                liveWorkoutService,
                liveWorkoutMessageService,
                liveWorkoutMapper,
                liveWorkoutMessageMapper,
                liveWorkoutEventPublisher
        );

        exerciseService = mock(ExerciseLibraryService.class);
        exerciseMapper = mock(ExerciseLibraryDtoDomain.class);
        exerciseController = new ExerciseLibraryController(exerciseService, exerciseMapper);

        userService = mock(UserService.class);
        authService = mock(AuthService.class);
        userMapper = mock(UserDtoDomain.class);
        jwtCookieService = mock(JwtCookieService.class);
        userController = new UserController(userService, userMapper, jwtCookieService);
        authController = new AuthController(authService, userMapper);

        progressService = mock(ProgressService.class);
        progressMapper = mock(ProgressDtoDomain.class);
        progressController = new ProgressController(progressService, progressMapper);

        historyService = mock(WorkoutHistoryService.class);
        historyMapper = mock(WorkoutHistoryDtoDomain.class);
        historyController = new com.zeki.demo.WorkoutHistory.controller.WorkoutHistoryController(historyService, historyMapper);
    }

    @Test
    void workoutControllerDelegatesAllOperationsThroughCurrentUser() {
        Workout workout = mock(Workout.class);
        WorkoutResponse response = workoutResponse();
        CreateWorkoutRequest createRequest = createWorkoutRequest();
        UpdateWorkoutRequest updateRequest = updateWorkoutRequest();

        when(workoutService.getMyWorkouts(EMAIL)).thenReturn(List.of(workout));
        when(workoutService.upcomingWorkouts(EMAIL)).thenReturn(List.of(workout));
        when(workoutService.getWorkoutById(id, EMAIL)).thenReturn(workout);
        when(workoutMapper.toResponse(workout)).thenReturn(response);
        when(workoutMapper.toDomain(createRequest, EMAIL)).thenReturn(workout);
        when(workoutService.createWorkout(workout)).thenReturn(workout);
        when(workoutService.updateWorkout(id, updateRequest, EMAIL)).thenReturn(workout);

        assertEquals(List.of(response), workoutController.getMyWorkouts(auth));
        assertEquals(List.of(response), workoutController.getUpcomingWorkouts(auth));
        assertEquals(response, workoutController.getWorkoutById(id, auth));
        assertEquals(response, workoutController.createWorkout(createRequest, auth));
        assertEquals(response, workoutController.updateWorkout(id, updateRequest, auth));
        workoutController.deleteWorkout(id, auth);

        verify(workoutService).deleteWorkout(id, EMAIL);
    }

    @Test
    void nutritionControllerDelegatesFoodAndEntryOperations() {
        Nutrition nutrition = mock(Nutrition.class);
        NutritionResponse response = nutritionResponse();
        FoodProductsResponse foods = foodsResponse();
        FoodProductResponse food = foodResponse();
        CreateNutritionRequest createRequest = createNutritionRequest();
        UpdateNutritionRequest updateRequest = updateNutritionRequest();
        FoodProductSearchRequest searchRequest = foodSearchRequest();
        FoodProductsRequest productsRequest = foodProductsRequest();

        when(nutritionService.getMyNutritionEntries(EMAIL)).thenReturn(List.of(nutrition));
        when(nutritionService.getNutritionById(id, EMAIL)).thenReturn(nutrition);
        when(nutritionMapper.toResponse(nutrition)).thenReturn(response);
        when(nutritionMapper.toDomain(createRequest, EMAIL)).thenReturn(nutrition);
        when(nutritionService.createNutrition(nutrition, "Europe/Istanbul")).thenReturn(nutrition);
        when(nutritionService.updateNutrition(id, updateRequest, EMAIL, "Europe/Istanbul")).thenReturn(nutrition);
        when(nutritionService.searchFoodProducts(searchRequest)).thenReturn(foods);
        when(nutritionService.getFoodProductByBarcode("123", "nl")).thenReturn(food);
        when(nutritionService.getFoodProductsByBrand("fitco", productsRequest)).thenReturn(foods);
        when(nutritionService.getFoodProductsByCategory("bars", productsRequest)).thenReturn(foods);

        assertEquals(List.of(response), nutritionController.getMyNutritionEntries(auth));
        assertEquals(foods, nutritionController.searchFoodProducts(searchRequest, auth));
        assertEquals(food, nutritionController.getFoodProductByBarcode("123", "nl", auth));
        assertEquals(foods, nutritionController.getFoodProductsByBrand("fitco", productsRequest, auth));
        assertEquals(foods, nutritionController.getFoodProductsByCategory("bars", productsRequest, auth));
        assertEquals(response, nutritionController.getNutritionById(id, auth));
        assertEquals(response, nutritionController.createNutrition(createRequest, auth, "Europe/Istanbul"));
        assertEquals(response, nutritionController.updateNutrition(id, updateRequest, auth, "Europe/Istanbul"));
        nutritionController.deleteNutrition(id, auth, "Europe/Istanbul");

        verify(nutritionService).deleteNutrition(id, EMAIL, "Europe/Istanbul");
    }

    @Test
    void liveWorkoutControllerBroadcastsRoomEvents() {
        Principal principal = principal();
        LiveWorkoutRoom room = LiveWorkoutRoom.create("room1", EMAIL, id, "Workout");
        LiveWorkoutMessage message = mock(LiveWorkoutMessage.class);
        LiveWorkoutRoomResponse response = liveWorkoutRoomResponse();
        LiveWorkoutMessageResponse messageResponse = liveWorkoutMessageResponse();
        LiveWorkoutRoomDetailResponse detailResponse = new LiveWorkoutRoomDetailResponse(
                response,
                List.of(messageResponse)
        );
        LiveWorkoutProgressRequest progress = liveWorkoutProgressRequest();
        LiveWorkoutMessageRequest messageRequest = new LiveWorkoutMessageRequest("Nice set");

        when(liveWorkoutService.createRoom(id, EMAIL)).thenReturn(room);
        when(liveWorkoutService.getRoom("room1")).thenReturn(room);
        when(liveWorkoutService.closeRoom("room1", EMAIL)).thenReturn(room);
        when(liveWorkoutService.joinRoom("room1", EMAIL)).thenReturn(room);
        when(liveWorkoutService.leaveRoom("room1", EMAIL)).thenReturn(room);
        when(liveWorkoutService.updateProgress("room1", EMAIL, progress)).thenReturn(room);
        when(liveWorkoutMessageService.getRecentMessages("room1", EMAIL)).thenReturn(List.of(message));
        when(liveWorkoutMessageService.sendMessage("room1", EMAIL, "Nice set")).thenReturn(message);
        when(liveWorkoutMapper.toResponse(room)).thenReturn(response);
        when(liveWorkoutMessageMapper.toResponse(message)).thenReturn(messageResponse);
        when(liveWorkoutMapper.toDetailResponse(room, List.of(messageResponse))).thenReturn(detailResponse);

        assertEquals(response, liveWorkoutController.createRoom(createLiveWorkoutRoomRequest(), auth));
        assertEquals(response, liveWorkoutController.getRoom("room1"));
        assertEquals(detailResponse, liveWorkoutController.getRoomDetails("room1", auth));
        assertEquals(List.of(messageResponse), liveWorkoutController.getMessages("room1", auth));
        assertEquals(response, liveWorkoutController.closeRoom("room1", auth));
        liveWorkoutController.joinRoom("room1", principal);
        liveWorkoutController.leaveRoom("room1", principal);
        liveWorkoutController.updateProgress("room1", progress, principal);
        liveWorkoutController.sendMessage("room1", messageRequest, principal);

        verify(liveWorkoutEventPublisher).publishRoomClosed("room1", EMAIL, response);
        verify(liveWorkoutEventPublisher).publishUserJoined("room1", EMAIL, response);
        verify(liveWorkoutEventPublisher).publishUserLeft("room1", EMAIL, response);
        verify(liveWorkoutEventPublisher).publishProgressUpdated("room1", EMAIL, response);
        verify(liveWorkoutEventPublisher).publishMessageSent("room1", EMAIL, messageResponse);
    }

    @Test
    void remainingControllersDelegateToServicesAndMappers() {
        assertExerciseLibraryControllerDelegates();
        assertUserControllerDelegates();
        assertAuthControllerDelegates();
        assertProgressControllerDelegates();
        assertWorkoutHistoryControllerDelegates();
    }

    private void assertExerciseLibraryControllerDelegates() {
        ExerciseLibrarySearchRequest searchRequest = exerciseSearchRequest();
        ExerciseLibraryListRequest listRequest = exerciseListRequest();
        ExerciseLibrarySearchCriteria searchCriteria = mock(ExerciseLibrarySearchCriteria.class);
        ExerciseLibraryListCriteria listCriteria = mock(ExerciseLibraryListCriteria.class);
        ExerciseLibrary library = mock(ExerciseLibrary.class);
        ExerciseLibraryItem exerciseItem = mock(ExerciseLibraryItem.class);
        ExerciseMuscle muscle = mock(ExerciseMuscle.class);
        ExerciseReference equipment = mock(ExerciseReference.class);
        ExerciseReference category = mock(ExerciseReference.class);
        ExerciseLibraryResponse exerciseResponse = exerciseLibraryResponse();
        ExerciseLibraryItemResponse exercise = mock(ExerciseLibraryItemResponse.class);
        List<ExerciseMuscleResponse> muscleResponses = List.of(exerciseMuscleResponse());
        List<ExerciseReferenceResponse> equipmentResponses = List.of(exerciseEquipmentResponse());
        List<ExerciseReferenceResponse> categoryResponses = List.of(exerciseCategoryResponse());

        when(exerciseMapper.toDomain(searchRequest)).thenReturn(searchCriteria);
        when(exerciseMapper.toDomain(listRequest)).thenReturn(listCriteria);
        when(exerciseService.searchExercises(searchCriteria)).thenReturn(library);
        when(exerciseService.getExerciseById(1)).thenReturn(exerciseItem);
        when(exerciseService.getMuscles(listCriteria)).thenReturn(List.of(muscle));
        when(exerciseService.getEquipment(listCriteria)).thenReturn(List.of(equipment));
        when(exerciseService.getCategories(listCriteria)).thenReturn(List.of(category));
        when(exerciseMapper.toResponse(library)).thenReturn(exerciseResponse);
        when(exerciseMapper.toResponse(exerciseItem)).thenReturn(exercise);
        when(exerciseMapper.toMuscleResponses(List.of(muscle))).thenReturn(muscleResponses);
        when(exerciseMapper.toReferenceResponses(List.of(equipment))).thenReturn(equipmentResponses);
        when(exerciseMapper.toReferenceResponses(List.of(category))).thenReturn(categoryResponses);

        assertEquals(exerciseResponse, exerciseController.searchExercises(searchRequest, auth));
        assertEquals(exercise, exerciseController.getExerciseById(1, auth));
        assertEquals("Chest", exerciseController.getMuscles(listRequest, auth).get(0).name());
        assertEquals("Bench", exerciseController.getEquipment(listRequest, auth).get(0).name());
        assertEquals("Strength", exerciseController.getCategories(listRequest, auth).get(0).name());
    }

    private void assertUserControllerDelegates() {
        User user = mock(User.class);
        UserResponse response = userResponse();
        UpdateUserRequest request = updateUserRequest();
        AccountDeletionRequest deletionRequest = new AccountDeletionRequest("StrongPassword123!");
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);

        when(userService.getCurrentUser(EMAIL)).thenReturn(user);
        when(userService.updateUser(EMAIL, request)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer access-token");

        assertEquals(response, userController.getCurrentUser(auth));
        assertEquals(response, userController.updateUser(request, auth));
        userController.deleteCurrentUser(deletionRequest, auth, httpRequest);
        verify(userService).deleteCurrentUser(EMAIL, "StrongPassword123!");
    }

    private void assertAuthControllerDelegates() {
        User user = mock(User.class);
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
        AuthResponse response = authResponse();

        when(userMapper.toDomain(any(RegisterRequest.class))).thenReturn(user);
        when(userMapper.toDomain(any(LoginRequest.class))).thenReturn(user);
        when(authService.register(user)).thenReturn(authenticatedUser);
        when(authService.login(user)).thenReturn(authenticatedUser);
        when(authService.refresh("refresh-token")).thenReturn(authenticatedUser);
        when(userMapper.toAuthResponse(authenticatedUser)).thenReturn(response);

        assertEquals(response, authController.register(registerRequest()));
        assertEquals(response, authController.login(loginRequest()));
        assertEquals(response, authController.refresh(refreshTokenRequest()));
    }

    private void assertProgressControllerDelegates() {
        Progress progress = mock(Progress.class);
        ProgressResponse response = progressResponse();

        when(progressService.getProgress(EMAIL)).thenReturn(progress);
        when(progressMapper.toResponse(progress)).thenReturn(response);

        assertEquals(response, progressController.getProgress(auth));
    }

    private void assertWorkoutHistoryControllerDelegates() {
        WorkoutHistory history = mock(WorkoutHistory.class);
        WorkoutHistoryResponse response = workoutHistoryResponse();

        when(historyService.getWorkoutHistory(EMAIL)).thenReturn(List.of(history));
        when(historyService.getWorkoutHistoryById(id, EMAIL)).thenReturn(history);
        when(historyMapper.toResponse(history)).thenReturn(response);

        assertEquals(List.of(response), historyController.getWorkoutHistory(auth));
        assertEquals(response, historyController.getWorkoutHistoryById(id, auth));
    }

    private Authentication authentication() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(EMAIL);
        return authentication;
    }

    private Principal principal() {
        return () -> EMAIL;
    }

    private CreateWorkoutRequest createWorkoutRequest() {
        return new CreateWorkoutRequest("Push", LocalDate.now(), 30, 3, 10, 40.0,
                null, null, WorkoutCategory.GENERAL, MuscleType.CHEST, List.of(), id);
    }

    private UpdateWorkoutRequest updateWorkoutRequest() {
        return new UpdateWorkoutRequest(id, "Pull", LocalDate.now(), 40, 4, 8,
                50.0, null, WorkoutCategory.STRENGTH, MuscleType.BACK, List.of(), null);
    }

    private WorkoutResponse workoutResponse() {
        return new WorkoutResponse(id, LocalDate.now(), 30, 3, 10, 20.0, "Workout", null, null,
                WorkoutCategory.GENERAL, MuscleType.CHEST, List.of(), LocalDate.now(), LocalDate.now());
    }

    private CreateNutritionRequest createNutritionRequest() {
        return new CreateNutritionRequest("Meal", LocalDate.now(), 500, 20, 50, 10,
                null, NutritionCategory.LUNCH, id);
    }

    private UpdateNutritionRequest updateNutritionRequest() {
        return new UpdateNutritionRequest(id, "Meal 2", LocalDate.now(), 600, 30, 60, 20,
                null, NutritionCategory.DINNER);
    }

    private NutritionResponse nutritionResponse() {
        return new NutritionResponse(id, "Meal", LocalDate.now(), 500, 20, 50, 10, null,
                NutritionCategory.LUNCH, LocalDate.now(), LocalDate.now());
    }

    private FoodProductsResponse foodsResponse() {
        return new FoodProductsResponse(List.of(), 0, 0);
    }

    private FoodProductResponse foodResponse() {
        return new FoodProductResponse("123", "Food", null, null, null, null, null, null, null,
                null, null, null, null, List.of(), List.of(), null, null, null, null, null, List.of());
    }

    private FoodProductSearchRequest foodSearchRequest() {
        return new FoodProductSearchRequest("rice", 10, 0, "en", "nl", true);
    }

    private FoodProductsRequest foodProductsRequest() {
        return new FoodProductsRequest(10, 0, "en", "nl", true);
    }

    private CreateLiveWorkoutRoomRequest createLiveWorkoutRoomRequest() {
        return new CreateLiveWorkoutRoomRequest(id);
    }

    private LiveWorkoutProgressRequest liveWorkoutProgressRequest() {
        return new LiveWorkoutProgressRequest(1, 2, "ACTIVE");
    }

    private ExerciseLibrarySearchRequest exerciseSearchRequest() {
        return new ExerciseLibrarySearchRequest("push", 10, 0, null, null, null);
    }

    private ExerciseLibraryListRequest exerciseListRequest() {
        return new ExerciseLibraryListRequest(10, 0);
    }

    private ExerciseLibraryResponse exerciseLibraryResponse() {
        return new ExerciseLibraryResponse(List.of(), 0, 0);
    }

    private ExerciseMuscleResponse exerciseMuscleResponse() {
        return new ExerciseMuscleResponse(1, "Chest", "Chest", true, null, null);
    }

    private ExerciseReferenceResponse exerciseEquipmentResponse() {
        return new ExerciseReferenceResponse(2, "Bench");
    }

    private ExerciseReferenceResponse exerciseCategoryResponse() {
        return new ExerciseReferenceResponse(3, "Strength");
    }

    private UserResponse userResponse() {
        return new UserResponse(id, "Owner", EMAIL, 2200, "ROLE_USER");
    }

    private UpdateUserRequest updateUserRequest() {
        return new UpdateUserRequest("Owner", EMAIL, "Password123!", 2200);
    }

    private AuthResponse authResponse() {
        return new AuthResponse(userResponse());
    }

    private RegisterRequest registerRequest() {
        return new RegisterRequest("Owner", EMAIL, "Password123!");
    }

    private LoginRequest loginRequest() {
        return new LoginRequest(EMAIL, "Password123!");
    }

    private RefreshTokenRequest refreshTokenRequest() {
        return new RefreshTokenRequest("refresh-token");
    }

    private ProgressResponse progressResponse() {
        return new ProgressResponse(LocalDate.now(), LocalDate.now(), 1.0, 1, 1,
                List.of(), List.of(), List.of(), null);
    }

    private WorkoutHistoryResponse workoutHistoryResponse() {
        return new WorkoutHistoryResponse(id, LocalDate.now(), 30, 3, 10, 20.0,
                "Workout", null, null, WorkoutCategory.GENERAL, MuscleType.CHEST, LocalDate.now(), LocalDate.now());
    }

    private LiveWorkoutRoomResponse liveWorkoutRoomResponse() {
        return new LiveWorkoutRoomResponse("room1", EMAIL, id, "Workout", List.of(), Instant.now(), Instant.now());
    }

    private LiveWorkoutMessageResponse liveWorkoutMessageResponse() {
        return new LiveWorkoutMessageResponse("message-1", "ROOM1", EMAIL, "owner", "Nice set", Instant.now());
    }
}
