package edu.lpnu.saas.organization.client;

import edu.lpnu.saas.organization.dto.InternalMembershipRequest;
import edu.lpnu.saas.organization.dto.UserOrganizationsResponse;
import edu.lpnu.saas.organization.exception.types.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IamServiceFallback implements IamServiceClient {

    @Override
    public void createInternalMembership(InternalMembershipRequest request, String token) {
        log.error("IAM Service недоступний. Неможливо створити membership для юзера {} в організації {}",
                request.getUserId(), request.getOrganizationId());

        throw new ServiceUnavailableException("Сервіс авторизації тимчасово недоступний. Спробуйте створити організацію пізніше.");
    }

    @Override
    public UserOrganizationsResponse getUserOrganizationIds(Long userId, String token) {
        log.error("IAM Service недоступний. Неможливо отримати список організацій для юзера {}", userId);
        throw new ServiceUnavailableException("Не вдалося завантажити список ваших організацій. Сервіс авторизації недоступний.");
    }
}