package com.jetcab.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

public abstract class BaseModelMapper<S, T extends Serializable> {

    public abstract T map(final S source);

    public S mapInverse(final T target) {
        return null;
    }

    public List<T> map(final List<S> from) {
        if (isEmpty(from)) {
            return Collections.emptyList();
        }
        return from.stream().map(this::map).collect(toList());
    }

    public PageableList<T> mapToPageableList(Page<S> page, Pageable pageable) {
        List<T> content = map(page.getContent());
        return PageableList.of(content, pageable, page.getTotalElements());
    }
}