package com.hexle.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class ChallengeService {

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final long TOKEN_EXPIRY_SECONDS = 7L * 24 * 60 * 60;
    private static final String ALL_ZEROS_KEY = "0000000000000000000000000000000000000000000000000000000000000000";

    private final SecretKey secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public ChallengeService(@Value("${app.challenge.secret:}") String secretHex) {
        byte[] keyBytes;
        if (secretHex == null || secretHex.isBlank() || secretHex.equals(ALL_ZEROS_KEY)) {
            // Generate a random ephemeral key — tokens won't survive restarts in dev
            keyBytes = new byte[32];
            new SecureRandom().nextBytes(keyBytes);
        } else {
            keyBytes = HexFormat.of().parseHex(secretHex);
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Encrypts {@code word:attempts:expiryEpochSeconds} using AES-256-GCM.
     * Token format: base64url(IV[12] + ciphertext + GCM-tag[16])
     */
    public String createToken(String word, int challengerAttempts) {
        long expiry = Instant.now().getEpochSecond() + TOKEN_EXPIRY_SECONDS;
        String payload = word + ":" + challengerAttempts + ":" + expiry;

        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] cipherTextAndTag = cipher.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            byte[] tokenBytes = new byte[iv.length + cipherTextAndTag.length];
            System.arraycopy(iv, 0, tokenBytes, 0, iv.length);
            System.arraycopy(cipherTextAndTag, 0, tokenBytes, iv.length, cipherTextAndTag.length);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create challenge token", e);
        }
    }

    public record ChallengeData(String word, int challengerAttempts) {}

    /**
     * Decodes and verifies a challenge token.
     *
     * @throws IllegalArgumentException if the token is invalid, tampered with, or expired
     */
    public ChallengeData decodeToken(String token) {
        try {
            byte[] tokenBytes = Base64.getUrlDecoder().decode(token);

            if (tokenBytes.length <= GCM_IV_LENGTH) {
                throw new IllegalArgumentException("Invalid token length");
            }

            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherTextAndTag = new byte[tokenBytes.length - GCM_IV_LENGTH];
            System.arraycopy(tokenBytes, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(tokenBytes, GCM_IV_LENGTH, cipherTextAndTag, 0, cipherTextAndTag.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            String payload = new String(cipher.doFinal(cipherTextAndTag), StandardCharsets.UTF_8);
            String[] parts = payload.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid token format");
            }

            String word = parts[0];
            int attempts = Integer.parseInt(parts[1]);
            long expiry = Long.parseLong(parts[2]);

            if (Instant.now().getEpochSecond() > expiry) {
                throw new IllegalArgumentException("Challenge token has expired");
            }

            return new ChallengeData(word, attempts);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or corrupted token", e);
        }
    }
}
