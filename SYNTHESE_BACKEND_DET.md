# 🎓 Synthèse Globale : Backend Service (Agenda-J2E)

Ce document rassemble tout ce qu'il faut savoir sur votre Backend pour votre présentation. Il est structuré selon le flux de données et l'ordre de révision recommandé.

---

## 1. La Carte d'Identité (Infrastructure)
*   **Rôle** : Gérer les utilisateurs, les tâches, la sécurité (JWT) et communiquer les changements à Kafka.
*   **Entrée (`BackendApplication.java`)** : Utilise `@SpringBootApplication` pour l'auto-configuration et `@EnableDiscoveryClient` pour être visible sur Eureka.
*   **Configuration (`pom.xml` / `application.properties`)** : Définit les dépendances (JPA, Web, Kafka, Security) et les connexions (MySQL, Kafka, Eureka).

---

## 2. L'Architecture en Couches (Le Flux de Données)

### A. La Couche Modèle (`model`) 🏛️
Représente les tables MySQL via **JPA/Hibernate**.
- **Annotations** : `@Entity`, `@Id`, `@ManyToOne` (relation User/Task).
- **Lombok** : `@Data` génère automatiquement les getters/setters.

### B. La Couche Accès aux Données (`repository`) 🗄️
Interfaces héritant de `JpaRepository`.
- **Force** : Génère automatiquement les requêtes SQL à partir du nom des méthodes (ex: `findByEmail`).

### C. La Couche Transport (`dto`) 📦
Objets légers pour les échanges réseau et Kafka.
- **Pourquoi ?** Sécurité (cacher le mot de passe) et découplage (séparer la base de données de l'API).
- **Spécial** : `TaskEvent` sert de "conteneur" pour Kafka (Action + Données).

### D. La Couche Logique (`service`) 🧠
C'est le cerveau qui orchestre tout.
- **Annotations** : `@Service` et `@Transactional` (garantit que si l'envoi Kafka échoue, l'enregistrement en base est annulé).
- **Kafka** : Appelle le `KafkaProducerService` pour envoyer les notifications de manière asynchrone.

### E. La Couche API (`controller`) 🌐
Point d'entrée pour Angular.
- **RESTful** : Utilise les verbes standards (GET, POST, PUT, DELETE).
- **Spring Security** : Injecte l'objet `Authentication` pour identifier l'utilisateur à partir du token JWT.

---

## 3. Le Pilier de Sécurité (JWT) 🔐
Système **Stateless** (SANS SESSION) :
1.  **Login** : Le serveur vérifie les identifiants et génère un jeton signé.
2.  **Filter (`JwtAuthFilter`)** : Intercepte chaque requête, valide le jeton avec `JwtUtils`.
3.  **Config (`SecurityConfig`)** : Définit que `/auth` est public et le reste est privé.

---

## 💡 Les Mots-Clés à placer à l'oral :
- **Découplage** : Le Backend ne sait pas qui reçoit le mail, il publie juste sur Kafka.
- **Persistance** : Utilisation d'Hibernate pour mapper les objets vers MySQL.
- **Asynchronisme** : Utilisation de Kafka pour ne pas bloquer l'utilisateur pendant l'envoi d'un mail.
- **State-less** : Sécurité JWT qui ne consomme pas de mémoire serveur pour les sessions.

---
> [!TIP]
> **Le secret de la réussite :** Montrez que chaque dossier a une responsabilité unique. C'est ce qu'on appelle le **SRP** (Single Responsibility Principle).
