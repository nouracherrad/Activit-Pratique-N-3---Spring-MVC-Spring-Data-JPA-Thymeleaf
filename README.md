# Activit-Pratique-N-3---Spring-MVC-Spring-Data-JPA-Thymeleaf
###Gestion des Patients - Application Spring Boot

## Description du projet
Ce projet est une application web de gestion des patients d'un hôpital développée en **Spring Boot** avec **Spring Data JPA**, **Thymeleaf** et **Bootstrap**. L'application permet de lister, rechercher et supprimer des patients.

## Technologies Utilisées
- **Spring Boot** (Framework principal)
- **Spring Data JPA** (Gestion de la persistance des données)
- **Hibernate** (ORM pour la gestion des entités)
- **Thymeleaf** (Moteur de template pour le frontend)
- **Bootstrap** (Framework CSS pour le design)
- **H2 Database** (Base de données en mémoire pour les tests)
- **Maven** (Gestionnaire de dépendances)

## Installation et Exécution
### Prérequis
- **Java 17+**
- **Maven**
- **IntelliJ IDEA** ou un autre IDE compatible


## Fonctionnalités
- **Ajout automatique des patients** (via `CommandLineRunner` dans `HopitalApplication.java`)
- **Affichage de la liste des patients** avec pagination
- **Recherche des patients par nom**
- **Suppression des patients**
- **Interface utilisateur avec Bootstrap**

## Explication du Code
### 1. **Entité `Patient`**
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
    private String name;
    private Date dateNaissance;
    private boolean malade;
    private int score;
}
```
- Annotation `@Entity` pour indiquer une classe persistante.
- `@Id` et `@GeneratedValue` pour l'identifiant unique auto-généré.

### 2. **Repository `PatientRepo`**
```java
public interface PatientRepo extends JpaRepository<Patient, Long> {
    Page<Patient> findByNameContains(String keyword, Pageable pageable);
    @Query("select p from Patient p where p.name like :x")
    Page<Patient> chercher(@Param("x") String keyword, Pageable pageable);
}
```
- `JpaRepository<Patient, Long>` pour les opérations CRUD.
- `findByNameContains()` pour la recherche dynamique.
- `@Query` pour une requête personnalisée.

### 3. **Contrôleur `PatientController`**
```java
@Controller
@AllArgsConstructor
public class PatientController {
    @Autowired
    private PatientRepo patientRepo;
    @GetMapping("/index")
    public String index(Model model, @RequestParam(name = "page",defaultValue = "0") int p,
    @RequestParam(name = "size" ,defaultValue = "4") int s,
    @RequestParam(name = "keyword" ,defaultValue = "") String kw){
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
        @RequestParam(value = "page", defaultValue = "0") int page){
        patientRepo.deleteById(id);
        return "redirect:/index?page="+page+"&keyword="+keyword;
    }
}
```
- `@GetMapping("/index")` pour afficher la liste paginée des patients.
- `@GetMapping("/delete")` pour supprimer un patient.

### 4. **Application Principale `HopitalApplication`**
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
        Patient patient = new Patient(null, "Mohamed", new Date(2000,10,10), true, 80);
        Patient patient1 = new Patient(null, "Noura", new Date(2000,10,10), true, 80);
        patientRepo.save(patient);
        patientRepo.save(patient1);
    }
}
```
- `CommandLineRunner` insère des données test à l'exécution.

### 5. **Template Thymeleaf `patient.html`**
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
                <a onclick="return confirm('Etes-vous sûr ?')" th:href="@{delete(id=${patient.id}, keyword=${keyword}, page=${currentPage})}" class="btn btn-danger">Supprimer</a>
            </td>
        </tr>
    </tbody>
</table>
```
### 6. ** la visualisation de la Template **
![image](https://github.com/user-attachments/assets/714dea4d-d539-4b0e-adfa-72966f05ddbe)


