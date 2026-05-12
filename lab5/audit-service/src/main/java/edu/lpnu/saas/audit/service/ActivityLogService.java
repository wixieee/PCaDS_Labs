package edu.lpnu.saas.audit.service;

import edu.lpnu.saas.audit.dto.PageActivityLogResponse;
import edu.lpnu.saas.audit.model.ActivityLog;
import edu.lpnu.saas.audit.repository.ActivityLogRepository;
import edu.lpnu.saas.audit.util.mapper.ActivityLogMapper;
import edu.lpnu.saas.common.dto.AuditMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper mapper;

    @RabbitListener(queues = "audit.queue")
    public void handleAuditMessage(AuditMessage message) {
        log.debug("Отримано подію аудиту: {}", message.getAction());

        ActivityLog activityLog = ActivityLog.builder()
                .userId(message.getUserId())
                .organizationId(message.getOrganizationId())
                .action(message.getAction())
                .ipAddress(message.getIpAddress())
                .build();

        activityLogRepository.save(activityLog);
    }

    public PageActivityLogResponse getOrganizationLogs(Long organizationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ActivityLog> logPage = activityLogRepository.findByOrganizationId(organizationId, pageable);
        return mapper.toPageResponse(logPage);
    }
}