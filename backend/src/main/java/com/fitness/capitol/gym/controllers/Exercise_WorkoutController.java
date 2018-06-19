package com.fitness.capitol.gym.controllers;

import com.fitness.capitol.gym.excpetions.ExerciseAlreadyExistsException;
import com.fitness.capitol.gym.model.Exercise;
import com.fitness.capitol.gym.model.User;
import com.fitness.capitol.gym.model.Workout;
import com.fitness.capitol.gym.model.Workout_Exercise;
import com.fitness.capitol.gym.service.ExerciseService;
import com.fitness.capitol.gym.service.UserService;
import com.fitness.capitol.gym.service.WorkoutService;
import com.fitness.capitol.gym.service.Workout_ExerciseService;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/exercises", produces = MediaType.APPLICATION_JSON_VALUE)
public class Exercise_WorkoutController {

    @Autowired
    private Workout_ExerciseService workout_exerciseService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private UserService userService;

    //    PROBAJ GOOOO OVA **************
    @RequestMapping(method = RequestMethod.GET)
    public List<Exercise> getAllExercisesByWorkout(@RequestParam("workoutDate") String workoutDate,
                                                   @RequestParam("username") String username) {
        User user = userService.findByUsername(username);
        String[] parts = workoutDate.split("\\.");
        long time = Long.parseLong(parts[0]) + Long.parseLong(parts[1]) + Long.parseLong(parts[2]);
        Date date = new Date();
        date.setTime(time);
        Workout workout = new Workout();
        workout = workoutService.findByDate(date, user);
        List<Workout_Exercise> workout_exercises =workout_exerciseService.findAllByWorkout(workout);
        List<Exercise> exercises = new ArrayList<>();
        for(Workout_Exercise we : workout_exercises){
            exercises.add(we.getExercise());
        }
        return exercises;
    }

    @RequestMapping(value = "/addExercise", method = RequestMethod.POST)
    public ResponseEntity addExercise(@RequestParam("workoutDate") String workoutDate,
                                      @RequestParam("name") String name,
                                      @RequestParam("username") String username) {
        User user;
        Exercise exercise;
        try {
            user = userService.findByUsername(username);
            exercise = exerciseService.findByName(name);

            if (exercise == null) {
                exercise = new Exercise();
                exercise.setName(name);
                exerciseService.save(exercise);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        String [] parts = workoutDate.split("\\.");
        long time = Long.parseLong(parts[0]) + Long.parseLong(parts[1]) + Long.parseLong(parts[2]);
        Date date = new Date();
        date.setTime(time);
        Workout workout = workoutService.findByDate(date, user);
        Workout_Exercise workout_exercise = new Workout_Exercise();
        workout_exercise.setExercise(exercise);
        workout_exercise.setWorkout(workout);
        workout_exerciseService.saveExerciseForWorkout(workout_exercise);
        return ResponseEntity.status(HttpStatus.OK).body("Exercise saved");

    }

}
