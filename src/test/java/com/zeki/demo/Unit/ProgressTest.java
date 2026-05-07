package com.zeki.demo.Unit;

import com.zeki.demo.progress.repository.ProgressRepository;
import com.zeki.demo.progress.service.ProgressService;
import com.zeki.demo.workouts.domain.Workout;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProgressTest {

    @Mock
    private ProgressRepository progressRepository;

    private ProgressService progressService;

}
