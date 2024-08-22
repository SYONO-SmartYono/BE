package paengbeom.syono.entity;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_USER("사용자"), ROLE_ADMIN("관리자");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
