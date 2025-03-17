package ma.enset.hopital;

import ma.enset.hopital.entities.Patient;
import ma.enset.hopital.repo.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Date;

@SpringBootApplication
public class HopitalApplication implements CommandLineRunner {
    @Autowired
    private PatientRepo patientRepo;
    public static void main(String[] args) {
        SpringApplication.run(HopitalApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
  patientRepo.save(Patient.builder().name("P1").dateNaissance(new Date(2000, 1, 1)).score(10).malade(true).build());
        patientRepo.save(Patient.builder().name("P2").dateNaissance(new Date(2000, 1, 1)).score(10).malade(true).build());
        patientRepo.save(Patient.builder().name("P3").dateNaissance(new Date(2000, 1, 1)).score(10).malade(true).build());
        patientRepo.save(Patient.builder().name("P4").dateNaissance(new Date(2000, 1, 1)).score(10).malade(true).build());
        patientRepo.save(Patient.builder().name("P5").dateNaissance(new Date(2000, 1, 1)).score(10).malade(true).build());

    }
}
