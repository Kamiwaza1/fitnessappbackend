package com.zeki.demo.exerciseLibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class exerciseDto {
    private String name;
    private String description;
    private String muscleGroup;
    private int repetitions;
    private int sets;
}