package com.junit.mapper;

import com.junit.dto.CreateUserDto;
import com.junit.entity.Gender;
import com.junit.entity.Role;
import com.junit.entity.User;
import com.junit.util.LocalDateFormatter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CreateUserMapper implements Mapper<CreateUserDto, User> {

    private static final CreateUserMapper INSTANCE = new CreateUserMapper();

    public static CreateUserMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public User map(CreateUserDto object) {
        return User.builder()
                .name(object.getName())
                .birthday(LocalDateFormatter.format(object.getBirthday()))
                .email(object.getEmail())
                .password(object.getPassword())
                .gender(Gender.find(object.getGender()).orElse(null))
                .role(Role.find(object.getRole()).orElse(null))
                .build();
    }
}
