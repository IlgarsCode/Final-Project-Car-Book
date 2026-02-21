package com.example.demo.services.impl;

import com.example.demo.dto.auth.RegisterDto;
import com.example.demo.model.RegisterOtp;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.enums.RoleName;
import com.example.demo.repository.RegisterOtpRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.EmailService;
import com.example.demo.services.RegisterOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterOtpServiceImpl implements RegisterOtpService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RegisterOtpRepository registerOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final int OTP_TTL_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;

    private final SecureRandom random = new SecureRandom();

    private String genOtp6() {
        int n = 100000 + random.nextInt(900000);
        return String.valueOf(n);
    }

    @Override
    @Transactional
    public void requestRegisterOtp(RegisterDto dto) {

        String email = dto.getEmail().trim().toLowerCase();
        String fullName = dto.getFullName().trim();

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Şifrə təkrarı uyğun deyil");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu email artıq mövcuddur");
        }

        registerOtpRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        String otp = genOtp6();
        String otpHash = passwordEncoder.encode(otp);

        String pendingPasswordHash = passwordEncoder.encode(dto.getPassword());

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_TTL_MINUTES);

        RegisterOtp record = registerOtpRepository.findByEmailIgnoreCase(email).orElse(null);

        if (record == null) {
            record = new RegisterOtp();
            record.setEmail(email);
        }

        record.setOtpHash(otpHash);
        record.setExpiresAt(expiresAt);
        record.setAttempts(0);
        record.setUsed(false);
        record.setVerifiedAt(null);
        record.setUsedAt(null);
        record.setCreatedAt(LocalDateTime.now());

        record.setFullName(fullName);
        record.setPasswordHash(pendingPasswordHash);

        registerOtpRepository.save(record);

        String subject = "CarBook - Qeydiyyat OTP";
        String text = """
                Salam!

                Qeydiyyatı tamamlamaq üçün OTP kodunuz:

                %s

                Kod %d dəqiqə etibarlıdır.

                Əgər bu siz deyilsinizsə, bu emaili nəzərə almayın.
                """.formatted(otp, OTP_TTL_MINUTES);

        emailService.sendOtpMail(email, subject, text);
    }

    @Override
    @Transactional
    public void verifyRegisterOtpAndCreateUser(String emailRaw, String code) {

        String email = emailRaw.trim().toLowerCase();

        RegisterOtp otp = registerOtpRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP tapılmadı. Yenidən göndər."));

        if (Boolean.TRUE.equals(otp.getUsed())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP artıq istifadə olunub. Yenidən göndər.");
        }

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP vaxtı bitib. Yenidən göndər.");
        }

        if (otp.getAttempts() >= MAX_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Çox cəhd etdiniz. Yenidən OTP göndərin.");
        }

        otp.setAttempts(otp.getAttempts() + 1);

        boolean ok = passwordEncoder.matches(code, otp.getOtpHash());
        if (!ok) {
            registerOtpRepository.save(otp);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP yanlışdır");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            otp.setUsed(true);
            otp.setUsedAt(LocalDateTime.now());
            otp.setVerifiedAt(LocalDateTime.now());
            registerOtpRepository.save(otp);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu email artıq mövcuddur");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(RoleName.ROLE_USER);
                    return roleRepository.save(r);
                });

        User user = new User();
        user.setEmail(email);
        user.setFullName(otp.getFullName());
        user.setPasswordHash(otp.getPasswordHash());
        user.getRoles().add(userRole);
        user.setIsActive(true);

        userRepository.save(user);

        otp.setVerifiedAt(LocalDateTime.now());
        otp.setUsed(true);
        otp.setUsedAt(LocalDateTime.now());
        registerOtpRepository.save(otp);
    }
}
