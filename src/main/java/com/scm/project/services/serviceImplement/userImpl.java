package com.scm.project.services.serviceImplement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.project.Entity.User;
import com.scm.project.helper.AppConstants;
import com.scm.project.helper.Helper;
import com.scm.project.helper.ResourceNotFound;
import com.scm.project.repository.UserRepo;
import com.scm.project.services.MailService;
import com.scm.project.services.userService;

@Service
public class userImpl implements userService {

    @Autowired
    private UserRepo userRepo;

    private Logger logger=LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MailService mailService;

    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;
     
    @Override
    public User saveUser(User user) {
        //generate userId
        String uId=UUID.randomUUID().toString();
        user.setId(uId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        
        String emailToken=UUID.randomUUID().toString();
        user.setEmailToken(emailToken);
        User saveUser=userRepo.save(user);

        String emailLink=Helper.getEmailVerificationLink(emailToken, baseUrl);
        logger.info("Verification link for {}: {}", saveUser.getEmail(), emailLink);

        // sendMail is @Async — fires in background, won't block registration response
        mailService.sendMail(saveUser.getEmail(), "Verify Email : SCM Contact Manager.", emailLink);

        return saveUser;
    }

    @Override
    public Optional<User> getUserById(String Id) {
       return userRepo.findById(Id);
    }

    @Override
    public Optional<User> updatUser(User user) {
        User user2=userRepo.findById(user.getId()).orElseThrow(()->new ResourceNotFound("user not Found."));
        user2.setName(user.getName());
        user2.setEmail(user.getEmail());
        user2.setPassword(user.getPassword());
        user2.setPhoneNumber(user.getPhoneNumber());
        user2.setAbout(user.getAbout());
        user2.setProfilePic(user.getProfilePic());
        user2.setEnable(user.isEnabled());
        user2.setEmailVarified(user.isEmailVarified());
        user2.setPhoneVarified(user.isPhoneVarified());
        user2.setProvider(user.getProvider());
        user2.setProviderId(user.getProviderId());

        User update= userRepo.save(user2);
        return Optional.ofNullable(update);
    }

    @Override
    public void deletUserById(String Id) {
        User user2=userRepo.findById(Id)
        .orElseThrow(()->new ResourceNotFound("user not Found."));
        userRepo.delete(user2);
    }

    @Override
    public boolean isUserExist(String userId) {
        User user2=userRepo.findById(userId).orElse(null);
        return user2!=null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User user=userRepo.findByEmail(email).orElse(null);
        return user!=null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();    
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }


}
