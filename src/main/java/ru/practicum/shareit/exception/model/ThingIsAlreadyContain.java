package ru.practicum.shareit.exception.model;

public class ThingIsAlreadyContain extends RuntimeException {
  public ThingIsAlreadyContain(String message) {
    super(message);
  }
}
