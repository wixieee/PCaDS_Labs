package edu.lpnu.saas.service;

import edu.lpnu.saas.dto.response.ResourceLimitResponse;
import edu.lpnu.saas.exception.types.LimitExceededException;
import edu.lpnu.saas.exception.types.NotFoundException;
import edu.lpnu.saas.model.ResourceLimit;
import edu.lpnu.saas.repository.ResourceLimitRepository;
import edu.lpnu.saas.util.mapper.ResourceLimitMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResourceLimitService {

    private final ResourceLimitRepository resourceLimitRepository;
    private final ResourceLimitMapper mapper;

    @Transactional
    public void consumeComment(Long organizationId) {
        ResourceLimit limit = resourceLimitRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new NotFoundException("Ліміти для організації не знайдено"));

        if (limit.getUsedCommentsCount() >= limit.getMaxComments()) {

            throw new LimitExceededException(
                    "Перевищено місячний ліміт коментарів (" + limit.getMaxComments() + "). " +
                            "Будь ласка, перейдіть на вищий тарифний план."
            );
        }

        limit.setUsedCommentsCount(limit.getUsedCommentsCount() + 1);
        resourceLimitRepository.save(limit);
    }

    public ResourceLimitResponse getLimits(Long organizationId) {
        return mapper.toResponse(resourceLimitRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new NotFoundException("Ліміти для організації не знайдено")));
    }
}