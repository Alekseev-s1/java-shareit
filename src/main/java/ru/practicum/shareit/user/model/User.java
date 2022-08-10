package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private long id;
    private String name;
    private String email;
}
