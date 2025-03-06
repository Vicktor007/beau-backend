package com.vic.ecommerce.service.impl;

import com.vic.ecommerce.model.VerificationCode;
import com.vic.ecommerce.repository.VerificationCodeRepository;
import com.vic.ecommerce.service.VerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class VerificationServiceImpl implements VerificationService {

    @Autowired
    private final VerificationCodeRepository verificationCodeRepository;

    VerificationServiceImpl(VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }


    @Override
    public VerificationCode createVerificationCode(String otp, String email) {
        VerificationCode isExist = verificationCodeRepository.findByEmail(email);
        if (isExist != null) {
            verificationCodeRepository.delete(isExist);
        }

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(1)); // Sets expiry date to 1 minute from now

        if (verificationCode.getExpiryDate() == null) {
            throw new IllegalArgumentException("Expiry date must not be null");
        }

        return verificationCodeRepository.save(verificationCode);
    }

    @Override
    public boolean isOtpExpired(String email) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);

        return verificationCode == null || verificationCode.getExpiryDate() == null || LocalDateTime.now().isAfter(verificationCode.getExpiryDate());
    }

}