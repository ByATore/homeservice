package com.homeservice.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    
    private List<T> list;
    private Long total;
    private Integer page;
    private Integer size;
    private Integer totalPages;
    
    public static <T> PageResult<T> empty() {
        return new PageResult<>(Collections.emptyList(), 0L, 1, 10, 0);
    }
    
    public static <T> PageResult<T> of(List<T> list, Long total, Integer page, Integer size) {
        int totalPages = (int) Math.ceil((double) total / size);
        return new PageResult<>(list, total, page, size, totalPages);
    }
}