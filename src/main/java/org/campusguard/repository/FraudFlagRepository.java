package org.campusguard.repository;

import org.campusguard.entity.FraudFlag;
import org.campusguard.entity.User;
import org.campusguard.entity.enums.FraudFlagStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudFlagRepository extends JpaRepository<FraudFlag, Long> {

    List<FraudFlag> findByStatusOrderByCreatedAtDesc(FraudFlagStatus status);

    List<FraudFlag> findByUser(User user);

    long countByStatus(FraudFlagStatus status);
}
