package ru.gorvat.book.controller.payload;

public record UpdateBookPayload(String title, String author, Integer publication) {
}
