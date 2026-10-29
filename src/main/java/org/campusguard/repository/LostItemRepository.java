package org.campusguard.repository;

import org.campusguard.entity.LostItem;
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
public interface LostItemRepository extends JpaRepository<LostItem, Long>, JpaSpecificationExecutor<LostItem> {

    List<LostItem> findByReportedByOrderByCreatedAtDesc(User user);

    Page<LostItem> findByStatus(ItemStatus status, Pageable pageable);

    long countByStatus(ItemStatus status);

    List<LostItem> findByStatusIn(List<ItemStatus> statuses);

    @Query("SELECT l FROM LostItem l WHERE l.status = 'LOST' OR l.status = 'MATCHED'")
    List<LostItem> findAllActiveLostItems();

    @Query("SELECT COUNT(l) FROM LostItem l WHERE l.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);
}
