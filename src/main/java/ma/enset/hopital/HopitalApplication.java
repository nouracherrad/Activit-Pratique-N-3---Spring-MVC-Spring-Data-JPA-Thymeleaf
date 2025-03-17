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
   Patient patient= new Patient();
   patient.setId(null);
   patient.setName("mohamed");
   patient.setDateNaissance(new Date(2000,10,10));
   patient.setMalade(true);
   patient.setScore(80);
   Patient patient1=new Patient();
   patient1.setId(null);
   patient1.setName("noura");
   patient1.setDateNaissance(new Date(2000,10,10));
   patient1.setMalade(true);
   patient1.setScore(80);

   patientRepo.save(patient);
   patientRepo.save(patient1);



    }
}
