package ma.enset.hopital;

import ma.enset.hopital.entities.Patient;
import ma.enset.hopital.repo.PatientRepo;
import ma.enset.hopital.security.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;
import java.sql.Date;

@SpringBootApplication
public class HopitalApplication implements CommandLineRunner {
    @Autowired
    private PatientRepo patientRepo;

    public static void main(String[] args) {
        SpringApplication.run(HopitalApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Patient patient = new Patient();
        patient.setId(null);
        patient.setName("mohamed");
        patient.setDateNaissance(new Date(100, 10, 10)); // 100 = 2000 (année - 1900)
        patient.setMalade(true);
        patient.setScore(80);

        Patient patient1 = new Patient();
        patient1.setId(null);
        patient1.setName("noura");
        patient1.setDateNaissance(new Date(100, 10, 10));
        patient1.setMalade(true);
        patient1.setScore(80);

        patientRepo.save(patient);
        patientRepo.save(patient1);
    }

  //  @Bean
    CommandLineRunner commandLineRunner(JdbcUserDetailsManager jdbcUserDetailsManager) {
        return args -> {
                UserDetails u1 = jdbcUserDetailsManager.loadUserByUsername("user11");
                if (u1 == null) {


                jdbcUserDetailsManager.createUser(
                        User.withUsername("user11")
                                .password(passwordEncoderr().encode("1234"))
                                .roles("USER")
                                .build()
                );
                }

            UserDetails u2= jdbcUserDetailsManager.loadUserByUsername("user22");
            if (u2 == null) {

                jdbcUserDetailsManager.createUser(
                        User.withUsername("user22")
                                .password(passwordEncoderr().encode("1234"))
                                .roles("USER")
                                .build()
                );
            }
            UserDetails u3 = jdbcUserDetailsManager.loadUserByUsername("admin2");
            if (u3 == null) {


                jdbcUserDetailsManager.createUser(
                        User.withUsername("admin2")
                                .password(passwordEncoderr().encode("1234"))
                                .roles("ADMIN", "USER")
                                .build()
                );}

        };
    }

    //@Bean
    PasswordEncoder passwordEncoderr() {
        return new BCryptPasswordEncoder();
    }
    //@Bean
        CommandLineRunner commandLineRunner(AccountService accountService) {
        return args -> {
        accountService.addNewRole("USER");
        accountService.addNewRole("ADMIN");
        accountService.addNewUser("user1", "1234", "user1@example.com", "1234");
        accountService.addNewUser("user2", "1234", "user2@example.com", "1234");
        accountService.addNewUser("admin", "1234", "admin@example.com", "1234");
        accountService.addRoleToUser("user1", "USER");
        accountService.addRoleToUser("user2", "USER");
        accountService.addRoleToUser("admin", "ADMIN");
};
    }}
