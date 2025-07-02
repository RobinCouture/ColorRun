package fr.esgi.robin.colorrun.business;

public enum Role {
    USER("participant"),
    ADMIN("admin"),
    ORGANISATEUR("organisateur");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role fromString(String value) {
        for (Role role : Role.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        return USER; // Par défaut
    }
}