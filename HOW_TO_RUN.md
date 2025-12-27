# 🚀 Guide de Démarrage (Startup Guide)

Suivez ces étapes dans l'ordre exact pour lancer toute l'application.

## 1. Infrastructure Kafka
Ouvrez un terminal à la racine du projet et lancez :
```powershell
docker-compose up -d
```

## 2. Services Backend (Spring Boot)
Ouvrez 4 terminaux séparés (ou utilisez des onglets) et lancez ces commandes dans cet ordre :

1. **Eureka Server** (Port 8761)
   ```powershell
   cd .\eureka-server\
   mvn spring-boot:run
   ```
2. **API Gateway** (Port 8080)
   ```powershell
   cd .\api-gateway\
   mvn spring-boot:run
   ```
3. **Backend Service** (Port 8081)
   ```powershell
   cd .\Agenda-J2E\
   mvn spring-boot:run
   ```
4. **Notification Service** (Port 8082)
   ```powershell
   cd .\notification-service\
   mvn spring-boot:run
   ```

## 3. Frontend (Angular)
Ouvrez un dernier terminal pour l'interface :
```powershell
cd .\agenda-angular\
npm start
```

---

## 🔍 Vérification
- **Eureka Dashboard** : Allez sur [http://localhost:8761](http://localhost:8761). Tous les services doivent apparaître comme "UP".
- **Application** : Allez sur [http://localhost:4200](http://localhost:4200).
- **Emails** : Vérifiez votre boîte mail après avoir créé une tâche !
