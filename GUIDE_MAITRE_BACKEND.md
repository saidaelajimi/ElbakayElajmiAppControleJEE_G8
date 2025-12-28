# 📔 Guide Complet de Révision : Backend Agenda-J2E

Ce document fusionne toutes les explications détaillées fournies lors de notre séance de travail. Il couvre l'intégralité du fonctionnement technique du Backend.

---

## 🏗️ 1. Architecture en Couches (Layered Architecture)
Le projet respecte une séparation stricte des responsabilités pour faciliter la maintenance et les tests.

### 🏛️ 1.1 Model (Les Entités)
Il représente vos tables MySQL en objets Java via **JPA/Hibernate**.
- **Fichiers** : `Task.java`, `User.java`.
- **Annotations Clés** :
    - `@Entity` : Définit la classe comme une table.
    - `@Id` & `@GeneratedValue` : Gèrent la clé primaire unique.
    - `@ManyToOne` : Définit la relation entre une tâche et son utilisateur (Clé étrangère).
    - `@Enumerated(EnumType.STRING)` : Enregistre les enums comme du texte (A_FAIRE) et non des chiffres.
- **Lombok** : `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` (évite d'écrire les Getters/Setters).

### 🗄️ 1.2 Repository (Le DAO)
C'est l'interface qui discute avec la base de données.
- **Fichiers** : `TaskRepository.java`, `UserRepository.java`.
- **Principe** : Héritage de `JpaRepository<Entity, IdType>`.
- **Atout** : Utilise les "Query Methods" (Spring génère le SQL à partir du nom de la méthode, ex: `findByEmail`).

### 📦 1.3 DTO (Data Transfer Object)
Objets de transport de données entre le serveur et le client.
- **Fichiers** : `TaskDTO.java`, `UserDTO.java`, `TaskEvent.java`.
- **Rôles** :
    - **Sécurité** : Ne pas exposer les mots de passe.
    - **Kafka** : `TaskEvent` emballe l'action (`CREATED`, `UPDATED`, `DELETED`) pour le service de notification.

### 🧠 1.4 Service (La Logique Métier)
C'est le "cerveau" qui orchestre les actions.
- **Fichiers** : `TaskService.java`, `KafkaProducerService.java`.
- **Annotations** : 
    - `@Service` : Enregistre la classe comme service Spring.
    - `@Transactional` : Garantit que si l'envoi Kafka échoue après une sauvegarde en base, la transaction est annulée.
- **Kafka** : Le service délègue l'envoi des notifications au `KafkaProducerService` pour être asynchrone.

### 🌐 1.5 Controller (L'API REST)
La façade qui reçoit les appels du Frontend Angular.
- **Fichiers** : `TaskController.java`, `AuthController.java`.
- **Annotations** :
    - `@RestController` : Renvoie du JSON.
    - `@RequestMapping("/tasks")` : Définit l'URL de base.
    - `@RequestBody` : Transforme le JSON entrant en objet Java.
    - `Authentication authentication` : Injecté par Spring Security pour identifier l'utilisateur via son JWT.

---

## 🔐 2. Sécurité et Authentification (JWT)
Système performant sans session (Stateless).

1.  **Le Gardien (`SecurityConfig.java`)** : Définit les accès (ex: `/auth/**` est public, le reste nécessite un token). Configure le filtre JWT.
2.  **L'Usine (`JwtUtils.java`)** : Contient les méthodes pour créer (Signer) et lire (Extraire les données) les tokens JWT.
3.  **Le Filtre (`JwtAuthFilter.java`)** : S'exécute à chaque requête. Il vérifie si le badge JWT présent dans le header `Authorization` est valide. S'il l'est, il authentifie l'utilisateur pour le reste du traitement.

---

## 🎡 3. Messagerie Asynchrone (Apache Kafka)
Utilisé pour le **Découplage** entre le Backend et les Notifications.

- **Producteur** : Le `KafkaProducerService` utilise un `KafkaTemplate` pour publier des messages sur le topic `task-events`.
- **Topic** : Un journal de messages où les données sont stockées temporairement.
- **Pourquoi ?** Pour que l'application reste rapide. Le Backend n'attend pas que le mail soit envoyé pour répondre à l'utilisateur.

---

## ⚙️ 4. Configuration (application.properties / YAML)
- **Base de données** : Connexion à MySQL, création automatique des tables (`ddl-auto=update`).
- **Eureka** : Adresse de l'annuaire pour que le service s'enregistre.
- **Kafka** : Adresse du serveur broker (localhost:9092).

---

## 🏁 Bilan : Les Forces de votre Backend
1.  **Clarté** : Architecture propre en couches.
2.  **Sécurité** : JWT robuste et gestion des accès précise.
3.  **Évolutivité** : Grâce aux DTOs et à Kafka, on peut facilement ajouter de nouvelles fonctionnalités sans tout casser.
4.  **Modernité** : Utilisation de Spring Boot 3 et des dernières bibliothèques J2E.
