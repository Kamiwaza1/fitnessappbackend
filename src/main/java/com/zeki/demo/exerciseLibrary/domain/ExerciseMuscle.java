package com.zeki.demo.exerciseLibrary.domain;

public record ExerciseMuscle(
        Integer id,
        String name,
        String nameEn,
        Boolean isFront,
        String imageUrlMain,
        String imageUrlSecondary
) {
}
