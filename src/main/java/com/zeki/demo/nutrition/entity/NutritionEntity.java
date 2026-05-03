package com.zeki.demo.nutrition.entity;

import com.zeki.demo.nutrition.enums.NutritionCategory;
import com.zeki.demo.users.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nutrition_entries")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "nutritionId")
public class NutritionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID nutritionId;

    @Column(name = "meal_name", nullable = false)
    private String mealName;

    @Column(name = "meal_date", nullable = false)
    private LocalDate mealDate;

    @Column(name = "calories", nullable = false)
    private Integer calories;

    @Column(name = "protein_grams", nullable = false)
    private Integer proteinGrams;

    @Column(name = "carbs_grams", nullable = false)
    private Integer carbsGrams;

    @Column(name = "fat_grams", nullable = false)
    private Integer fatGrams;

    @Column(name = "notes")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private NutritionCategory category;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
