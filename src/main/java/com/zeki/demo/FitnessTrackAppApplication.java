package com.zeki.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
		"com.zeki.demo.WorkoutHistory.repository.jpa",
		"com.zeki.demo.liveworkouts.repository.jpa",
		"com.zeki.demo.nutrition.repository.jpa",
		"com.zeki.demo.users.repository.jpa",
		"com.zeki.demo.workouts.repository.jpa"
})
@EnableMongoRepositories(basePackages = "com.zeki.demo.liveworkouts.repository.mongo")
public class FitnessTrackAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitnessTrackAppApplication.class, args);
	}

}
