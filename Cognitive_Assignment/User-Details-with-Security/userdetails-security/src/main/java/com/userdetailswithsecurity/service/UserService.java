package com.userdetailswithsecurity.service;

import com.userdetailswithsecurity.model.PasswordResetToken;
import com.userdetailswithsecurity.model.UserInfo;
import com.userdetailswithsecurity.model.dto.PasswordDto;
import com.userdetailswithsecurity.repository.PasswordTokenRepository;
import com.userdetailswithsecurity.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class UserService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    public String addUser(UserInfo userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "user added to system ";
    }

    public void createPasswordResetTokenForUser(UserInfo user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(user,token);
        passwordTokenRepository.save(myToken);
    }

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    public String changePassword(PasswordDto passwordDto) {

       PasswordResetToken passwordResetToken= passwordTokenRepository.findByToken(passwordDto.getToken());
       UserInfo user = passwordResetToken.getUser();

        if(user!=null) {
           user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
           repository.save(user);
           return "Password Reset Successfully";
        } else {
            return "Invalid Details try again";
        }
    }
}
