package ma.enset.hopital.Web;

import lombok.AllArgsConstructor;
import ma.enset.hopital.entities.Patient;
import ma.enset.hopital.repo.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Controller
@AllArgsConstructor
public class PatientController {
    @Autowired
    private PatientRepo patientRepo;
    @GetMapping("/index")
    public String index(Model model,
        @RequestParam(name = "page",defaultValue = "0") int p,
        @RequestParam(name = "size" ,defaultValue = "4") int s,
        @RequestParam(name = "keyword" ,defaultValue = "") String kw){
        Page<Patient> pagePatients=patientRepo.findByNameContains(kw, PageRequest.of(p, s));
        model.addAttribute("listPatients",pagePatients.getContent());
        model.addAttribute("pages",new int [pagePatients.getTotalPages()]);
        model.addAttribute("currentPage",p);
        model.addAttribute("keyword",kw);
        return "patient";
    }
}
