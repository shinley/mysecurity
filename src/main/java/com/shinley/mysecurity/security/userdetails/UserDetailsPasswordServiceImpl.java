package com.shinley.mysecurity.security.userdetails;

import com.shinley.mysecurity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsPasswordServiceImpl implements UserDetailsPasswordService {
    private final UserRepo userRepo;
    /**
     * 进行密码升级
     * @param userDetails
     * @param newPassword
     * @return
     */
    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {

        return userRepo.findOptionalByUsername(userDetails.getUsername())
                .map(user ->  (UserDetails)userRepo.save(user.withPassword(newPassword)))
                .orElse(userDetails);
    }
}
