package com.example.Daily.Habit.mapper;

import org.mapstruct.*;
import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.entity.Role;

import com.example.Daily.Habit.model.UserPublicResponse;
import com.example.Daily.Habit.model.UserUpdateRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(target = "habits", ignore = true),
            @Mapping(target = "ongoingHabits", ignore = true),
    })
    User toEntity(com.example.Daily.Habit.model.User dto);

    @Mappings({
            @Mapping(target = "role", expression = "java(mapRoleToPublic(entity.getRole()))"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "phoneNumber", source = "phoneNumber")
    })
    UserPublicResponse toPublic(User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "role", ignore = true),
            @Mapping(target = "userId", ignore = true),
            @Mapping(target = "habits", ignore = true),
            @Mapping(target = "ongoingHabits", ignore = true)
    })
    void patchFromRequest(UserUpdateRequest dto, @MappingTarget User entity);


    default Role map(com.example.Daily.Habit.model.User.RoleEnum role) {
        if (role == null) return null;
        return switch (role) {
            case ADMIN -> Role.ADMIN;
            case USER  -> Role.USER;
        };
    }

    default com.example.Daily.Habit.model.User.RoleEnum map(Role role) {
        if (role == null) return null;
        return switch (role) {
            case ADMIN -> com.example.Daily.Habit.model.User.RoleEnum.ADMIN;
            case USER  -> com.example.Daily.Habit.model.User.RoleEnum.USER;
        };
    }

    default UserPublicResponse.RoleEnum mapRoleToPublic(Role role) {
        if (role == null) return null;
        return switch (role) {
            case ADMIN -> UserPublicResponse.RoleEnum.ADMIN;
            case USER  -> UserPublicResponse.RoleEnum.USER;
        };
    }
}
