package com.revature.revtickets.repository;

import com.revature.revtickets.document.ActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
    List<ActivityLog> findByUserIdOrderByTimestampDesc(Long userId);
}
