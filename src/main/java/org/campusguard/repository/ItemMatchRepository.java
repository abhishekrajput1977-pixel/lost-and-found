package org.campusguard.repository;

import org.campusguard.entity.FoundItem;
import org.campusguard.entity.ItemMatch;
import org.campusguard.entity.LostItem;
import org.campusguard.entity.User;
import org.campusguard.entity.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemMatchRepository extends JpaRepository<ItemMatch, Long> {

    Optional<ItemMatch> findByLostItemAndFoundItem(LostItem lostItem, FoundItem foundItem);

    List<ItemMatch> findByLostItemOrderByMatchScoreDesc(LostItem lostItem);

    List<ItemMatch> findByFoundItemOrderByMatchScoreDesc(FoundItem foundItem);

    @Query("SELECT m FROM ItemMatch m WHERE m.lostItem.reportedBy = :user OR m.foundItem.reportedBy = :user ORDER BY m.matchScore DESC")
    List<ItemMatch> findAllByUser(@Param("user") User user);

    List<ItemMatch> findByStatusOrderByMatchScoreDesc(MatchStatus status);

    long countByStatus(MatchStatus status);
}
