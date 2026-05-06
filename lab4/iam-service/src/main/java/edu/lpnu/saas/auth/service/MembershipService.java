package edu.lpnu.saas.auth.service;

import edu.lpnu.saas.auth.dto.InternalMembershipRequest;
import edu.lpnu.saas.auth.dto.InviteMemberRequest;
import edu.lpnu.saas.auth.dto.UpdateRoleRequest;
import edu.lpnu.saas.auth.model.Membership;
import edu.lpnu.saas.auth.model.User;
import edu.lpnu.saas.auth.repository.MembershipRepository;
import edu.lpnu.saas.auth.repository.UserRepository;
import edu.lpnu.saas.common.dto.UserInvitedEvent;
import edu.lpnu.saas.common.exception.types.AlreadyExistsException;
import edu.lpnu.saas.common.exception.types.GeneralWebException;
import edu.lpnu.saas.common.exception.types.NotFoundException;
import edu.lpnu.saas.common.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void inviteMember(Long organizationId, InviteMemberRequest request, Long currentUserId) {
        Membership inviterMembership = membershipRepository.findByOrganizationIdAndUserId(organizationId, currentUserId)
                .orElseThrow(() -> new GeneralWebException("Вас не знайдено в цій організації", HttpStatus.FORBIDDEN));

        Role requestedRole = Role.valueOf(request.getRole().name());

        if (inviterMembership.getRole().getLevel() <= requestedRole.getLevel()) {
            throw new GeneralWebException("Ви можете видавати лише ролі, нижчі за вашу власну", HttpStatus.FORBIDDEN);
        }

        Optional<User> existingUserOpt = userRepository.findByEmail(request.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (membershipRepository.findByOrganizationIdAndUserId(organizationId, existingUser.getId()).isPresent()) {
                throw new AlreadyExistsException("Користувач вже є учасником цієї організації");
            }
            createMembershipRecord(existingUser.getId(), organizationId, requestedRole);
        } else {
            String randomPassword = RandomStringUtils.secure().nextAlphanumeric(10);
            User newUser = User.builder()
                    .email(request.getEmail())
                    .firstName("New")
                    .lastName("User")
                    .password(passwordEncoder.encode(randomPassword))
                    .isActive(true)
                    .build();
            newUser = userRepository.save(newUser);

            createMembershipRecord(newUser.getId(), organizationId, requestedRole);

            String orgName = "Організація #" + organizationId;
            UserInvitedEvent event = UserInvitedEvent.builder()
                    .email(newUser.getEmail())
                    .orgName(orgName)
                    .password(randomPassword)
                    .build();

            rabbitTemplate.convertAndSend("notification.exchange", "notification.email.invite", event);
            log.info("Створено нового користувача та відправлено подію запрошення: {}", newUser.getEmail());
        }
    }

    @Transactional
    public void updateMemberRole(Long organizationId, Long targetUserId, UpdateRoleRequest request, Long currentUserId) {
        Membership targetMembership = findByOrganizationAndUserId(organizationId, targetUserId);
        Membership updaterMembership = findByOrganizationAndUserId(organizationId, currentUserId);

        Role requestedRole = Role.valueOf(request.getRole().name());

        if (updaterMembership.getRole().getLevel() <= targetMembership.getRole().getLevel() ||
                updaterMembership.getRole().getLevel() <= requestedRole.getLevel()) {
            throw new GeneralWebException("У вас недостатньо прав для цієї дії", HttpStatus.FORBIDDEN);
        }

        targetMembership.setRole(requestedRole);
        membershipRepository.save(targetMembership);
    }

    @Transactional
    public void removeMember(Long organizationId, Long targetUserId, Long currentUserId) {
        Membership targetMembership = findByOrganizationAndUserId(organizationId, targetUserId);
        Membership updaterMembership = findByOrganizationAndUserId(organizationId, currentUserId);

        if (updaterMembership.getRole().getLevel() <= targetMembership.getRole().getLevel()) {
            throw new GeneralWebException("У вас недостатньо прав для видалення цього учасника", HttpStatus.FORBIDDEN);
        }

        membershipRepository.deleteById(targetMembership.getId());
    }

    @Transactional
    public void createInternalMembership(InternalMembershipRequest request) {
        Role requestedRole = Role.valueOf(request.getRole().name());
        createMembershipRecord(request.getUserId(), request.getOrganizationId(), requestedRole);
    }

    private void createMembershipRecord(Long userId, Long orgId, Role role) {
        Membership membership = Membership.builder()
                .userId(userId)
                .organizationId(orgId)
                .role(role)
                .build();
        membershipRepository.save(membership);
    }

    private Membership findByOrganizationAndUserId(Long organizationId, Long userId) {
        return membershipRepository.findByOrganizationIdAndUserId(organizationId, userId)
                .orElseThrow(() -> new NotFoundException("Учасника не знайдено"));
    }
}