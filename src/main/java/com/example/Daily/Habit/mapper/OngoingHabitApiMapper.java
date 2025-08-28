package com.example.Daily.Habit.mapper;


import com.example.Daily.Habit.entity.OngoingHabit;
import com.example.Daily.Habit.entity.OngoingHabitStatus;
import com.example.Daily.Habit.model.OngoingHabitResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OngoingHabitApiMapper {


     // Convert OngoingHabit entity to API response model

    public OngoingHabitResponse toApiResponse(OngoingHabit entity) {
        if (entity == null) {
            return null;
        }

        OngoingHabitResponse response = new OngoingHabitResponse();
        response.setOngoingHabitId(entity.getId());
        response.setHabitId(entity.getHabit().getId());
        response.setHabitName(entity.getHabit().getName());
        response.setCounter(entity.getCounter());
        response.setStatus(OngoingHabitResponse.StatusEnum.valueOf(entity.getStatus().name()));
        response.setTarget(entity.getHabit().getTarget());
        return response;
    }





     //Convert list of OngoingHabit entities to API response models

    public List<OngoingHabitResponse> toApiResponseList(List<OngoingHabit> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toApiResponse)
                .collect(Collectors.toList());
    }
}