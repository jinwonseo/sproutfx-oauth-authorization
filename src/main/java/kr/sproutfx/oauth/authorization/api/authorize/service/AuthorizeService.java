package kr.sproutfx.oauth.authorization.api.authorize.service;

import kr.sproutfx.common.security.configuration.jwt.component.JwtProvider;
import kr.sproutfx.oauth.authorization.api.authorize.exception.*;
import kr.sproutfx.oauth.authorization.api.client.entity.Client;
import kr.sproutfx.oauth.authorization.api.client.enumeration.ClientStatus;
import kr.sproutfx.oauth.authorization.api.member.entity.Member;
import kr.sproutfx.oauth.authorization.api.member.enumeration.MemberStatus;
import kr.sproutfx.oauth.authorization.configuration.crypto.CryptoUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthorizeService {

    private final CryptoUtils cryptoUtils;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthorizeService(CryptoUtils cryptoUtils, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.cryptoUtils = cryptoUtils;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public String decryptClientSecret(String encryptedClientSecret) {
        return this.cryptoUtils.decrypt(encryptedClientSecret);
    }

    public String encryptClientSecret(String clientSecret) {
        return this.cryptoUtils.encrypt(clientSecret);
    }

    public String getTokenType() {
        return this.jwtProvider.getAuthorizationType();
    }

    public String createToken(String subject, String audience, String secret, long validityInSeconds) {
        String token = this.jwtProvider.createToken(subject, audience, secret, validityInSeconds);

        if (StringUtils.isBlank(token)) {
            throw new TokenCreationFailedException();
        }

        return token;
    }

    public String extractSubject(String secret, String audience, String token) {
        String subject = this.jwtProvider.extractSubject(secret, audience, token);

        if (StringUtils.isBlank(token)) {
            throw new ExtractSubjectFailedException();
        }

        return subject;
    }

    public Long extractTokenExpiresInSeconds(String secret, String audience, String token) {
        Long expiresInSeconds = this.jwtProvider.extractExpiresInSeconds(secret, audience, token);

        if (expiresInSeconds.equals(-1L)) {
            throw new ExtractExpiresInSecondsFailedException();
        }

        return expiresInSeconds;
    }

    public Boolean validateToken(String secret, String audience, String token) {
        return this.jwtProvider.validateToken(secret, audience, token);
    }

    public void validateClientStatus(Client client) {
        if (ClientStatus.BLOCKED.equals(client.getStatus())) {
            throw new BlockedClientException();
        } else if (ClientStatus.DEACTIVATED.equals(client.getStatus())) {
            throw new DeactivatedClientException();
        } else if (ClientStatus.PENDING_APPROVAL.equals(client.getStatus())) {
            throw new PendingApprovalClientException();
        } else if (!ClientStatus.ACTIVE.equals(client.getStatus())) {
            throw new ClientAccessDeniedException();
        }
    }

    public void validateMemberPassword(Member member, String password) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new UnauthorizedException();
        } else if (member.getPasswordExpired().isBefore(LocalDateTime.now())) {
            throw new PasswordExpiredException();
        }
    }

    public void validateMemberStatus(Member member) {
        if (MemberStatus.BLOCKED.equals(member.getStatus())) {
            throw new BlockedMemberException();
        } else if (MemberStatus.DEACTIVATED.equals(member.getStatus())) {
            throw new DeactivatedMemberException();
        } else if (MemberStatus.PENDING_APPROVAL.equals(member.getStatus())) {
            throw new PendingApprovalMemberException();
        } else if (!MemberStatus.ACTIVE.equals(member.getStatus())) {
            throw new MemberAccessDeniedException();
        }
    }
}
