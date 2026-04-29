package edu.lpnu.saas.service;

import edu.lpnu.saas.dto.response.ActivityLogResponse;
import edu.lpnu.saas.model.ActivityLog;
import edu.lpnu.saas.repository.ActivityLogRepository;
import edu.lpnu.saas.util.mapper.ActivityLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    public Page<ActivityLogResponse> getOrganizationLogs(Long organizationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ActivityLog> logPage = activityLogRepository.findByOrganizationId(organizationId, pageable);

        return logPage.map(mapper::toResponse);
    }
}