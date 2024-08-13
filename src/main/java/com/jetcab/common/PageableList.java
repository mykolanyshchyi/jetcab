package com.jetcab.common;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

@Data
public class PageableList<T extends Serializable> implements Serializable {

    public static final String PAGE = "page";
    public static final String LIMIT = "limit";

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private Sort sort;

    private PageableList() {
    }

    public static <T extends Serializable> PageableList<T> of(List<T> content, Pageable pageable, long total) {
        PageableList<T> list = new PageableList<>();

        list.content = content;
        if (pageable != null && pageable.isPaged()) {
            list.pageNumber = pageable.getPageNumber() + 1;
            list.pageSize = pageable.getPageSize();
            list.sort = pageable.getSort();
        }
        list.totalElements = total;

        return list;
    }
}