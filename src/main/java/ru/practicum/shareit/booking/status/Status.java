package ru.practicum.shareit.booking.status;

public enum Status {
    WAITING("waiting"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CANCELLED("cancelled");

    private String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
