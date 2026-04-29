package edu.lpnu.saas.service;

import edu.lpnu.saas.dto.request.InviteMemberRequest;
import edu.lpnu.saas.dto.request.UpdateRoleRequest;
import edu.lpnu.saas.exception.types.AlreadyExistsException;
import edu.lpnu.saas.exception.types.AuthorizationException;
import edu.lpnu.saas.exception.types.NotFoundException;
import edu.lpnu.saas.model.Membership;
import edu.lpnu.saas.model.Organization;
import edu.lpnu.saas.model.User;
import edu.lpnu.saas.repository.MembershipRepository;
import edu.lpnu.saas.repository.OrganizationRepository;
import edu.lpnu.saas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmtpEmailService emailService;

    @Transactional
    public void inviteMember(Long organizationId, InviteMemberRequest request, Long currentUserId) {
        Membership inviterMembership = membershipRepository.findByOrganizationIdAndUserId(organizationId, currentUserId)
                .orElseThrow(() -> new AuthorizationException("Вас не знайдено в цій організації"));

        if (inviterMembership.getRole().getLevel() <= request.getRole().getLevel()) {
            throw new AuthorizationException("Ви можете видавати лише ролі, нижчі за вашу власну");
        }

        Optional<User> existingUserOpt = userRepository.findByEmail(request.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (membershipRepository.findByOrganizationIdAndUserId(organizationId, existingUser.getId()).isPresent()) {
                throw new AlreadyExistsException("Користувач вже є учасником цієї організації");
            }

            Membership membership = Membership.builder()
                    .userId(existingUser.getId())
                    .organizationId(organizationId)
                    .role(request.getRole())
                    .build();
            membershipRepository.save(membership);

        } else {
            Organization org = organizationRepository.findById(organizationId)
                    .orElseThrow(() -> new NotFoundException("Організацію не знайдено"));

            String randomPassword = RandomStringUtils.secure().nextAlphanumeric(10);

            User newUser = User.builder()
                    .email(request.getEmail())
                    .firstName("New")
                    .lastName("User")
                    .password(passwordEncoder.encode(randomPassword))
                    .isActive(true)
                    .build();
            newUser = userRepository.save(newUser);

            Membership membership = Membership.builder()
                    .userId(newUser.getId())
                    .organizationId(organizationId)
                    .role(request.getRole())
                    .build();
            membershipRepository.save(membership);

            emailService.sendInvitationWithCredentialsEmail(newUser.getEmail(), org.getName(), randomPassword);
        }
    }

    @Transactional
    public void updateMemberRole(Long organizationId, Long targetUserId, UpdateRoleRequest request, Long currentUserId) {
        Membership targetMembership = findByOrganizationAndUserId(organizationId, targetUserId);
        Membership updaterMembership = findByOrganizationAndUserId(organizationId, currentUserId);

        int updaterLevel = updaterMembership.getRole().getLevel();
        int targetCurrentLevel = targetMembership.getRole().getLevel();
        int newRoleLevel = request.getRole().getLevel();

        if (updaterLevel <= targetCurrentLevel || updaterLevel <= newRoleLevel) {
            throw new AuthorizationException("У вас недостатньо прав для цієї дії над цим користувачем");
        }

        targetMembership.setRole(request.getRole());
        membershipRepository.save(targetMembership);
    }

    @Transactional
    public void removeMember(Long organizationId, Long targetUserId, Long currentUserId) {
        Membership targetMembership = findByOrganizationAndUserId(organizationId, targetUserId);
        Membership updaterMembership = findByOrganizationAndUserId(organizationId, currentUserId);

        if (updaterMembership.getRole().getLevel() <= targetMembership.getRole().getLevel()) {
            throw new AuthorizationException("У вас недостатньо прав для видалення цього учасника");
        }

        membershipRepository.deleteById(targetMembership.getId());
    }

    private Membership findByOrganizationAndUserId(Long organizationId, Long userId) {
        return membershipRepository.findByOrganizationIdAndUserId(organizationId, userId)
                .orElseThrow(() -> new NotFoundException("Учасника не знайдено"));
    }
}