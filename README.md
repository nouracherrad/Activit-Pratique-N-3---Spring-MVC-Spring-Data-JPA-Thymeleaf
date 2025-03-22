# Activit-Pratique-N-3---Spring-MVC-Spring-Data-JPA-Thymeleaf
## Gestion des Patients - Application Spring Boot

## Description du projet
Ce projet est une application web de gestion des patients développée avec **Spring Boot**, **Spring Data JPA**, **Thymeleaf**, et **Bootstrap**. Il permet de gérer les patients d'un hôpital en offrant des fonctionnalités telles que l'ajout, la suppression, la recherche et l'affichage paginé des patients. Voici une explication détaillée des différentes parties du projet :

---

### 1. **Entité `Patient`**
L'entité `Patient` représente un patient dans la base de données. Elle est annotée avec `@Entity` pour indiquer qu'elle est persistante. Les annotations Lombok (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Builder`) simplifient la gestion des getters, setters, constructeurs et du pattern Builder.

```java
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Size(min=4, max=40)
    private String name;
    private Date dateNaissance;
    private boolean malade;
    @DecimalMin("1")
    private int score;
}
```

- **Validation des champs** :
  - `@NotEmpty` : Le champ `name` ne doit pas être vide.
  - `@Size(min=4, max=40)` : Le nom doit contenir entre 4 et 40 caractères.
  - `@DecimalMin("1")` : Le score doit être supérieur ou égal à 1.

---

### 2. **Repository `PatientRepo`**
Le repository `PatientRepo` étend `JpaRepository` pour fournir des méthodes CRUD (Create, Read, Update, Delete) par défaut. Il inclut également des méthodes personnalisées pour la recherche de patients par nom.

```java
public interface PatientRepo extends JpaRepository<Patient, Long> {
    Page<Patient> findByNameContains(String keyword, Pageable pageable);
    @Query("select p from Patient p where p.name like :x")
    Page<Patient> chercher(@Param("x") String keyword, Pageable pageable);
}
```

- **`findByNameContains`** : Recherche des patients dont le nom contient un mot-clé.
- **`@Query`** : Permet d'écrire des requêtes SQL personnalisées.

---

### 3. **Contrôleur `PatientController`**
Le contrôleur `PatientController` gère les requêtes HTTP et interagit avec le repository pour manipuler les données des patients.

```java
@Controller
@AllArgsConstructor
public class PatientController {
    @Autowired
    private PatientRepo patientRepo;

    @GetMapping("/index")
    public String index(Model model, 
                        @RequestParam(name = "page", defaultValue = "0") int p,
                        @RequestParam(name = "size", defaultValue = "4") int s,
                        @RequestParam(name = "keyword", defaultValue = "") String kw) {
        Page<Patient> pagePatients = patientRepo.findByNameContains(kw, PageRequest.of(p, s));
        model.addAttribute("listPatients", pagePatients.getContent());
        model.addAttribute("pages", new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentPage", p);
        model.addAttribute("keyword", kw);
        return "patient";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") long id,
                         @RequestParam(value = "keyword", defaultValue = "") String keyword,
                         @RequestParam(value = "page", defaultValue = "0") int page) {
        patientRepo.deleteById(id);
        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }

    @PostMapping("/save")
    public String savePatient(Model model, @Valid Patient patient, BindingResult bindingResult,
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        if (bindingResult.hasErrors()) return "formPatients";
        patientRepo.save(patient);
        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/editPatient")
    public String editPatient(Model model, Long id, String keyword, int page) {
        Patient patient = patientRepo.findById(id).orElse(null);
        if (patient == null) throw new RuntimeException("Patient introuvable");
        model.addAttribute("patient", patient);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        return "editPatient";
    }
}
```

- **`index`** : Affiche la liste paginée des patients.
- **`delete`** : Supprime un patient et redirige vers la liste des patients.
- **`savePatient`** : Sauvegarde un patient après validation des données.
- **`editPatient`** : Affiche un formulaire pour modifier un patient existant.

---

### 4. **Application Principale `HopitalApplication`**
La classe principale `HopitalApplication` initialise l'application et insère des données de test au démarrage grâce à l'interface `CommandLineRunner`.

```java
@SpringBootApplication
public class HopitalApplication implements CommandLineRunner {
    @Autowired
    private PatientRepo patientRepo;

    public static void main(String[] args) {
        SpringApplication.run(HopitalApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Patient patient = new Patient(null, "Mohamed", new Date(2000, 10, 10), true, 80);
        Patient patient1 = new Patient(null, "Noura", new Date(2000, 10, 10), true, 80);
        patientRepo.save(patient);
        patientRepo.save(patient1);
    }
}
```

---

### 5. **Templates Thymeleaf**
Les templates Thymeleaf sont utilisés pour générer les vues HTML. Voici un aperçu des fonctionnalités principales :

#### **`patient.html`**
- Affiche la liste des patients avec pagination et un formulaire de recherche.
- Inclut un bouton pour supprimer un patient.

```html
<form th:action="@{index}" method="get">
    <label>Keyword:</label>
    <input class="form-control" type="text" name="keyword" th:value="${keyword}">
    <button type="submit" class="btn btn-info">Chercher</button>
</form>
<table class="table">
    <thead>
        <tr>
            <th>Id</th>
            <th>Nom</th>
            <th>Date de naissance</th>
            <th>Malade</th>
            <th>Score</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="patient : ${listPatients}">
            <td th:text="${patient.id}"></td>
            <td th:text="${patient.name}"></td>
            <td th:text="${patient.dateNaissance}"></td>
            <td th:text="${patient.malade}"></td>
            <td th:text="${patient.score}"></td>
            <td>
                <a onclick="return confirm('Etes-vous sûr ?')" 
                   th:href="@{delete(id=${patient.id}, keyword=${keyword}, page=${currentPage})}" 
                   class="btn btn-danger">Supprimer</a>
            </td>
        </tr>
    </tbody>
</table>
```
### 6.**la visualisation de la Template**
![image](https://github.com/user-attachments/assets/714dea4d-d539-4b0e-adfa-72966f05ddbe)
#### **`formPatients.html`**
- Formulaire pour ajouter ou modifier un patient.
- Validation des champs avec affichage des erreurs.

```html
<form th:action="@{save(keyword=${keyword}, page=${currentPage})}" method="post">
    <div class="form-group">
        <label>id</label>
        <input type="hidden" name="id" th:value="${patient.id}" class="form-control">
    </div>
    <div class="form-group">
        <label for="name">Nom</label>
        <input type="text" name="name" th:value="${patient.name}" id="name" class="form-control">
        <span class="text-danger" th:errors="${patient.name}"></span>
    </div>
    <div class="form-group">
        <label for="dateNaissance">Date Naissance</label>
        <input type="date" name="dateNaissance" th:value="${patient.dateNaissance}" id="dateNaissance" class="form-control">
        <span class="text-danger" th:errors="${patient.dateNaissance}"></span>
    </div>
    <div class="form-group">
        <label for="malade">Malade</label>
        <input type="checkbox" name="malade" th:field="${patient.malade}" id="malade" class="form-control">
        <span class="text-danger" th:errors="${patient.malade}"></span>
    </div>
    <div class="form-group">
        <label for="score">Score</label>
        <input type="number" name="score" th:value="${patient.score}" id="score" class="form-control">
        <span class="text-danger" th:errors="${patient.score}"></span>
    </div>
    <button type="submit" class="btn btn-primary">Save</button>
</form>
```

---

### 6. **Validation des Formulaires**
La validation des formulaires est gérée par Spring Boot avec les annotations `@Valid` et `BindingResult`. Si des erreurs sont détectées, l'utilisateur est redirigé vers le formulaire avec les messages d'erreur appropriés.
![image](https://github.com/user-attachments/assets/56d819ad-a747-4ecd-b592-61e79fb63a12)
![image](https://github.com/user-attachments/assets/eb5b4d35-a776-4b53-8f0c-6f85344bf414)

---

### 7. **Base de Données H2**
La base de données H2 est utilisée pour les tests. Elle est configurée dans `application.properties` pour fonctionner en mémoire.

---

### 8. **Bootstrap pour le Design**
Bootstrap est utilisé pour styliser l'interface utilisateur, rendant l'application responsive et moderne.

---

### Conclusion
Ce projet est une application complète de gestion des patients, mettant en œuvre les meilleures pratiques de développement avec Spring Boot. Il inclut la validation des formulaires, la pagination, la recherche et une interface utilisateur intuitive grâce à Thymeleaf et Bootstrap.



