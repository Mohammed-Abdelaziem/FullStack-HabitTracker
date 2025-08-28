package com.example.Daily.Habit.mapper;

import com.example.Daily.Habit.entity.OngoingHabitStatus;
import com.example.Daily.Habit.model.*;
import org.mapstruct.*;
import com.example.Daily.Habit.entity.Habit;
import com.example.Daily.Habit.entity.Frequency;

@Mapper(componentModel = "spring")
public interface HabitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "frequency", expression = "java(mapFrequency(dto.getFrequency()))")
    @Mapping(target = "target", source = "target")
    @Mapping(target = "progress", constant = "0.0")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "ongoingHabits", ignore = true)
    Habit toEntity(HabitCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(
            target = "frequency",
            expression = "java(dto.getFrequency() == null ? entity.getFrequency() : mapFrequency(dto.getFrequency()))"
    )
    void updateEntityFromRequest(HabitUpdateRequest dto, @MappingTarget Habit entity);

    default HabitPublicResponse toPublicResponse(Habit entity) {
        if (entity == null) return null;
        HabitPublicResponse res = new HabitPublicResponse();
        res.setId(entity.getId());
        res.setName(entity.getName());
        res.setTarget(entity.getTarget());
        if (entity.getFrequency() != null) {
            res.setFrequency(HabitPublicResponse.FrequencyEnum.valueOf(entity.getFrequency().name()));
        }
        res.setProgress(entity.getProgress());
        return res;
    }

    default HabitDetailsResponse toDetailsResponse(Habit entity) {
        if (entity == null) return null;
        HabitDetailsResponse res = new HabitDetailsResponse();
        res.setId(entity.getId());
        res.setName(entity.getName());
        res.setTarget(entity.getTarget());
        if (entity.getFrequency() != null) {
            res.setFrequency(HabitDetailsResponse.FrequencyEnum.valueOf(entity.getFrequency().name()));
        }
        res.setProgress(entity.getProgress());
        return res;
    }

    default Frequency mapFrequency(HabitCreateRequest.FrequencyEnum freq) {
        return (freq == null) ? null : Frequency.valueOf(freq.name());
    }

    default Frequency mapFrequency(HabitUpdateRequest.FrequencyEnum freq) {
        return (freq == null) ? null : Frequency.valueOf(freq.name());
    }
    @ValueMappings({
            @ValueMapping(source = "INCOMPLETE", target = "INCOMPLETE"),
            @ValueMapping(source = "IN_PROGRESS", target = "IN_PROGRESS"),
            @ValueMapping(source = "COMPLETED", target = "COMPLETED"),
            @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    })
    ProgressResponse.StatusEnum mapOngoingHabitStatusToStatusEnum(OngoingHabitStatus status);
}
