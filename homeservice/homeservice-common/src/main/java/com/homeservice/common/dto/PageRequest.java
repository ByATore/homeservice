package com.homeservice.common.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class PageRequest implements Serializable {
    
    private Integer page = 1;
    private Integer size = 10;
    private String sortField;
    private String sortOrder = "desc";
    
    public Integer getOffset() {
        return (page - 1) * size;
    }
}