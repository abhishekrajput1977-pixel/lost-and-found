package org.campusguard.repository;

import org.campusguard.entity.FoundItem;
import org.campusguard.entity.User;
import org.campusguard.entity.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Long>, JpaSpecificationExecutor<FoundItem> {

    List<FoundItem> findByReportedByOrderByCreatedAtDesc(User user);

    Page<FoundItem> findByStatus(ItemStatus status, Pageable pageable);

    long countByStatus(ItemStatus status);

    List<FoundItem> findByStatusIn(List<ItemStatus> statuses);

    @Query("SELECT f FROM FoundItem f WHERE f.status = 'FOUND' OR f.status = 'MATCHED'")
    List<FoundItem> findAllActiveFoundItems();

    @Query("SELECT COUNT(f) FROM FoundItem f WHERE f.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);
}
