package ma.enset.hopital.security.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ma.enset.hopital.security.entities.AppRole;
import ma.enset.hopital.security.entities.AppUser;
import ma.enset.hopital.security.repo.AppRoleRepository;
import ma.enset.hopital.security.repo.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;
    @Override
    public AppUser addNewUser(String username, String password, String email, String confirmPassword) {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser != null)
            throw new RuntimeException("User already exists");
        if (!password.equals(confirmPassword))
                throw new RuntimeException("Passwords do not match");
        AppUser user = AppUser.builder()
                .userId(UUID.randomUUID().toString())
                .username(username)
                .password(passwordEncoder.encode(password))
                .email( email)
                .build();

        AppUser savedUser = appUserRepository.save(user);

        return savedUser;
    }



    @Override
    public AppRole addNewRole(String role) {
        AppRole appRole =appRoleRepository.findById(role).orElse(null);
        if (appRole != null)
            throw new RuntimeException("Role already exists");
        appRole = AppRole.builder()
                .role(role)
                .build();
        return appRoleRepository.save(appRole);

    }

    @Override
    public void addRoleToUser(String username, String role) {
     AppUser appUser = appUserRepository.findByUsername(username);
      AppRole appRole = appRoleRepository.findById(role).get();
      appUser.getRoles().add(appRole);
    }

    @Override
    public void removeRoleFromUser(String username, String role) {
 AppUser loadUser = appUserRepository.findByUsername(username);
 AppRole appRole = appRoleRepository.findById(role).get();
 loadUser.getRoles().remove(appRole);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

}
