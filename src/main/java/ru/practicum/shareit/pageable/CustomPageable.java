package ru.practicum.shareit.pageable;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class CustomPageable implements Pageable {
    private final int offset;
    private final int limit;
    private final Sort sort;

    private CustomPageable(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public static Pageable of(int from, int size) {
        return new CustomPageable(from, size, Sort.unsorted());
    }

    public static Pageable of(int from, int size, Sort sort) {
        return new CustomPageable(from, size, sort);
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new CustomPageable(offset + limit, limit, sort);
    }

    public Pageable previous() {
        return hasPrevious() ? of(offset - limit, limit, sort) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new CustomPageable(0, limit, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new CustomPageable(offset + limit * pageNumber, limit, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}
