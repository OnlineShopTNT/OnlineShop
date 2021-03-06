package com.tnt.onlineshop.security.impl;

import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.security.SecurityService;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;


public class DefaultSecurityService implements SecurityService {

    private static final Random RANDOM = new SecureRandom();
    private final SecretKeyFactory secretKeyFactory;
    private final String algorithm = "PBKDF2WithHmacSHA512";

    public DefaultSecurityService() {
        try {
            secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error during initialisation of secretKeyFactory", e);
        }
    }

    @Override
    public User createUserWithHashedPassword(String email, char[] password) {
        byte[] salt = generateSalt();
        int iterations = 10000;
        String hashedPassword = hashPassword(password, salt, iterations);
        User user = new User();
        user.setEmail(email);
        user.setHash(hashedPassword);
        user.setSalt(Base64.getEncoder().encodeToString(salt));
        user.setIterations(iterations);
        return user;
    }

    @Override
    public boolean checkPasswordAgainstUserPassword(User user, char[] password) {
        boolean isSame = false;
        if (user != null) {
            isSame = (user.getHash().equals(hashPassword(password,
                    Base64.getDecoder().decode(user.getSalt()), user.getIterations())));
        }
        return isSame;
    }

    String hashPassword(char[] password, byte[] salt, int iterations) {
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, iterations, 256);
            Arrays.fill(password, '\u0000');
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            byte[] result = secretKey.getEncoded();
            return Base64.getEncoder().encodeToString(result);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Error during hashing password", e);
        }
    }

    byte[] generateSalt() {
        byte[] salt = new byte[20];
        RANDOM.nextBytes(salt);
        return salt;
    }

}
