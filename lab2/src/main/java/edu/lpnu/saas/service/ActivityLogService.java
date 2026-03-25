package edu.lpnu.saas.service;

import edu.lpnu.saas.dto.response.ActivityLogResponse;
import edu.lpnu.saas.model.ActivityLog;
import edu.lpnu.saas.repository.ActivityLogRepository;
import edu.lpnu.saas.util.mapper.ActivityLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper mapper;

    @Async
    public void logAction(Long userId, Long organizationId, String action, String ipAddress) {
        ActivityLog activityLog = ActivityLog.builder()
                .userId(userId)
                .organizationId(organizationId)
                .action(action)
                .ipAddress(ipAddress)
                .build();

        activityLogRepository.save(activityLog);
    }

    public List<ActivityLogResponse> getOrganizationLogs(Long organizationId) {
        return activityLogRepository.findByOrganizationId(organizationId).stream()
                .sorted(Comparator.comparing(ActivityLog::getCreatedAt).reversed())
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}