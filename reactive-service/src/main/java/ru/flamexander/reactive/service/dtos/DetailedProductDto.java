package ru.flamexander.reactive.service.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DetailedProductDto {
    private final Long id;
    private final String name;
    private final String description;

    public DetailedProductDto(Long id, String name) {
        this.id = id;
        this.name = name;
        this.description = "";
    }
}
