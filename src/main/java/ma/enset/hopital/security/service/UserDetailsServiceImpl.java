package ma.enset.hopital.security.service;

import lombok.AllArgsConstructor;
import ma.enset.hopital.security.entities.AppUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@AllArgsConstructor

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
private AccountService accountService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser=accountService.loadUserByUsername(username);
        if(appUser==null) throw new UsernameNotFoundException(String.format("user %s not found",username));
        //appUser.getRoles().stream().map(r->r.getRole()).toArray(String[]::new);
        UserDetails userDetails= User.withUsername(appUser.getUsername()).password(appUser.getPassword()).roles(appUser.getRoles().stream().map(r->r.getRole()).toArray(String[]::new)).build();
        return userDetails;
    }
}
