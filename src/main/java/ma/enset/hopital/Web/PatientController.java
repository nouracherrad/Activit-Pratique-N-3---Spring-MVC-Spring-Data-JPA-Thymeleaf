package ma.enset.hopital.Web;

import ma.enset.hopital.entities.Patient;
import ma.enset.hopital.repo.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
@Controller
public class PatientController {
    @Autowired
    private PatientRepo patientRepo;
    @GetMapping("/patient")
    public String index(Model model){
        List<Patient> patientList=patientRepo.findAll();
        model.addAttribute("patients",patientList);
        return "patient";
    }
}
