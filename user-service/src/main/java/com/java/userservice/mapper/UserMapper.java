package com.java.userservice.mapper;

import com.java.userservice.controller.dto.RegisterRequestDTO;
import com.java.userservice.controller.dto.RegisterResponseDTO;
import com.java.userservice.controller.dto.UserRequestDTO;
import com.java.userservice.controller.dto.UserResponseDTO;
import com.java.userservice.enums.Role;
import com.java.userservice.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User userDTOToUser(UserRequestDTO dto);

    UserResponseDTO userToUserResponseDTO(User user);

    @Mapping(source = "role", target = "role", qualifiedByName = "stringToRole")
    User registerRequestDTOToUser(RegisterRequestDTO dto);

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    RegisterResponseDTO userToRegisterResponseDTO(User user);

    @Named("stringToRole")
    static Role stringToRole(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            return Role.USER;
        }
    }

    @Named("roleToString")
    static String roleToString(Role role) {
        return role.name();
    }

}
