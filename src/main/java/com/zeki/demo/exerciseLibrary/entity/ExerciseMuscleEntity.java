package com.zeki.demo.exerciseLibrary.entity;

public record ExerciseMuscleEntity(
        Integer id,
        String name,
        String nameEn,
        Boolean isFront,
        String imageUrlMain,
        String imageUrlSecondary
) {
}
