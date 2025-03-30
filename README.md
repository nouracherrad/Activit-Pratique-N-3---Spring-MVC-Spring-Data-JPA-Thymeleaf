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
### 7.Configuration de la Sécurité avec Spring Security

1. Fichiers de Configuration

securityConfig.java

Ce fichier configure la sécurité de l'application en utilisant Spring Security.

Encodeur de mot de passe : Utilisation de BCryptPasswordEncoder pour stocker les mots de passe de manière sécurisée.

Gestion des utilisateurs : Trois utilisateurs sont définis en mémoire (deux avec le rôle USER et un avec ADMIN, USER).

Gestion des autorisations :

Accès libre aux ressources statiques (/webjars/**, H2).

Authentification requise pour toutes les autres pages.

Redirection vers une page d’accès refusé (/notAuthorized) si l'utilisateur n'a pas les autorisations nécessaires.

package ma.enset.hopital.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class securityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user1").password(passwordEncoder().encode("1234")).roles("USER").build(),
                User.withUsername("user2").password(passwordEncoder().encode("1234")).roles("USER").build(),
                User.withUsername("admin").password(passwordEncoder().encode("1234")).roles("ADMIN", "USER").build()
        );
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin(formLogin ->
            formLogin.loginPage("/login")
                     .loginProcessingUrl("/login")
                     .defaultSuccessUrl("/user/index")
                     .permitAll()
        );
        httpSecurity.rememberMe();
        httpSecurity.authorizeRequests().requestMatchers("/webjars/**", "H2").permitAll();
        httpSecurity.authorizeRequests().anyRequest().authenticated();
        httpSecurity.exceptionHandling().accessDeniedPage("/notAuthorized");
        return httpSecurity.build();
    }
}

2. Interface de Connexion

login.html

Cette page HTML permet aux utilisateurs de se connecter.

Champs : Nom d'utilisateur et mot de passe.

Option "Remember me" : Permet de garder la session active.

Bootstrap : Utilisation de la bibliothèque pour une interface responsive.

<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Authentification</title>
    <link rel="stylesheet" href="/webjars/bootstrap/5.2.3/css/bootstrap.min.css">
    <script src="/webjars/bootstrap/5.2.3/js/bootstrap.bundle.js"></script>
</head>
<body>
<div class="row mt-3">
    <div class="col-md-6 offset-3">
        <div class="card">
            <div class="card-header">Authentification</div>
            <div class="card-body">
                <form method="post" th:action="@{/login}">
                    <div class="mb-3 mt-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" name="username" id="username" class="form-control">
                    </div>
                    <div class="mb-3 mt-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" name="password" id="password" class="form-control">
                    </div>
                    <div class="form-check mb-3">
                        <label class="form-check-label">
                            <input class="form-check-input" type="checkbox" name="remember-me">
                            Remember me
                        </label>
                    </div>
                    <button type="submit" class="btn btn-primary">Login</button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>

3. Gestion des Autorisations

securityController.java

Ce contrôleur gère les pages d'authentification et d'accès refusé.

/login : Redirige vers la page de connexion.

/notAuthorized : Page affichée en cas de refus d'accès.

package ma.enset.hopital.Web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class securityController {
    @GetMapping("/notAuthorized")
    public String notAuthorized() {
        return "notAuthorized";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
![image](https://github.com/user-attachments/assets/82eea331-489f-4146-9602-837de3e7d545)


4. Page d'Accès Refusé

notAuthorized.html

Cette page s'affiche lorsque l'utilisateur tente d'accéder à une ressource sans les droits requis.

Message d'erreur clair.

Utilisation de Thymeleaf Layouts pour un affichage homogène.

<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="template1" xmlns:sec="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>Accès Refusé</title>
    <link rel="stylesheet" href="/webjars/bootstrap/5.2.3/css/bootstrap.min.css">
</head>
<body>
<div layout:fragment="content">
    <div class="alert alert-danger m-3">
        <h1>Not Authorized</h1>
    </div>
</div>
</body>
</html>

Ce guide couvre la configuration complète de Spring Security avec gestion des utilisateurs, authentification et autorisation.

### 8. **Base de Données H2**
La base de données H2 est utilisée pour les tests. Elle est configurée dans `application.properties` pour fonctionner en mémoire.

---

### 9. **Bootstrap pour le Design**
Bootstrap est utilisé pour styliser l'interface utilisateur, rendant l'application responsive et moderne.

---

### Conclusion
Ce projet est une application complète de gestion des patients, mettant en œuvre les meilleures pratiques de développement avec Spring Boot. Il inclut la validation des formulaires, la pagination, la recherche et une interface utilisateur intuitive grâce à Thymeleaf et Bootstrap.



