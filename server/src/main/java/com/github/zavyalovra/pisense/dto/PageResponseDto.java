package com.github.zavyalovra.pisense.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Страница элементов вместе с метаданными пагинации.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private long from;
    private int size;
    private long totalElements;
    private int totalPages;

    public static <T> PageResponseDto<T> of(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber() * (long) page.getSize(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
