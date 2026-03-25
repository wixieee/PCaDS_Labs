package edu.lpnu.saas.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    OWNER(4),
    ADMIN(3),
    ANALYST(2),
    VIEWER(1);

    private final int level;
}