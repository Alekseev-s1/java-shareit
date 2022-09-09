package ru.practicum.shareit.requests.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findItemRequestsByRequestor_IdIsNot(long userId, Pageable pageable);

    List<ItemRequest> findItemRequestsByRequestor_Id(long userId, Sort sort);
}
