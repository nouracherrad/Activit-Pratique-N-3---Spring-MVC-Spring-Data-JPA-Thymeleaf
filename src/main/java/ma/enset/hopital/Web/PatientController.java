package ma.enset.hopital.Web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.enset.hopital.entities.Patient;
import ma.enset.hopital.repo.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
    @GetMapping("/delete")
    public  String delete(
            @RequestParam("id")  long id,
            @RequestParam(value = "keyword" ,defaultValue = "")  String keyword,
            @RequestParam(value = "page",defaultValue = "0") int page){
        patientRepo.deleteById(id);
        return "redirect:/index?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/")
    public String home(){
        return "redirect:/index";
    }
   @GetMapping("/formPatients")
    public String formPatients(Model model){
        model.addAttribute("patient",new Patient());
        return "formPatients";}

    @PostMapping("/save")
    public String savePatient(Model model, @Valid Patient patient , BindingResult bindingResult,
    @RequestParam(name = "page",defaultValue = "0") int page,
                              @RequestParam(name = "keyword" ,defaultValue = "") String keyword){
        if (bindingResult.hasErrors()) return "formPatients";
        patientRepo.save(patient);
        return "redirect:/index?page="+page+"&keyword="+keyword; // Redirige vers la liste des patients après la sauvegarde
    }
    @GetMapping("/editPatient")
    public String editPatient(Model model,  Long id , String keyword,int page){
        Patient patient=patientRepo.findById(id).orElse(null);
        if(patient==null) throw new RuntimeException("Patient introuvable");
        model.addAttribute("patient",patient);
        model.addAttribute("keyword",keyword);
        model.addAttribute("page",page);
        return "editPatient"; // Redirige vers la liste des patients après la sauvegarde
    }
}
