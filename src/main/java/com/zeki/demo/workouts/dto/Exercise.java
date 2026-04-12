package com.zeki.demo.workouts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Exercise {
    private String name;
    private String type;
    private String tool;
}
