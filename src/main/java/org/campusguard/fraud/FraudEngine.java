package org.campusguard.fraud;

import org.campusguard.entity.FraudFlag;
import org.campusguard.entity.TrustScore;
import org.campusguard.entity.User;
import org.campusguard.entity.enums.FraudFlagStatus;
import org.campusguard.entity.enums.FraudSeverity;
import org.campusguard.repository.FraudFlagRepository;
import org.campusguard.repository.TrustScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FraudEngine {

    private final TrustScoreRepository trustScoreRepository;
    private final FraudFlagRepository fraudFlagRepository;

    public FraudEngine(TrustScoreRepository trustScoreRepository, FraudFlagRepository fraudFlagRepository) {
        this.trustScoreRepository = trustScoreRepository;
        this.fraudFlagRepository = fraudFlagRepository;
    }

    @Transactional
    public void adjustTrustScore(User user, int delta) {
        TrustScore score = trustScoreRepository.findByUser(user)
                .orElseGet(() -> new TrustScore(user, 100));

        int newScore = Math.max(0, Math.min(200, score.getScore() + delta));
        score.setScore(newScore);
        trustScoreRepository.save(score);
    }

    @Transactional
    public FraudFlag flagSuspiciousActivity(User user, String itemType, Long itemId, String reason, FraudSeverity severity) {
        // Adjust trust score negatively
        int delta = severity == FraudSeverity.CRITICAL ? -25 :
                    severity == FraudSeverity.HIGH ? -15 :
                    severity == FraudSeverity.MEDIUM ? -10 : -5;
        adjustTrustScore(user, delta);

        FraudFlag flag = new FraudFlag();
        flag.setUser(user);
        flag.setItemType(itemType);
        flag.setItemId(itemId);
        flag.setReason(reason);
        flag.setSeverity(severity);
        flag.setStatus(FraudFlagStatus.PENDING_REVIEW);

        return fraudFlagRepository.save(flag);
    }

    public int getUserTrustScore(User user) {
        return trustScoreRepository.findByUser(user)
                .map(TrustScore::getScore)
                .orElse(100);
    }
}
