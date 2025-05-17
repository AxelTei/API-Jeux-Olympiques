# 🏟️ API Billetterie Jeux Olympiques Paris 2024

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Java-21-orange" alt="Java"/>
  <img src="https://img.shields.io/badge/License-MIT-blue" alt="License"/>
  <img src="https://img.shields.io/badge/Status-En%20développement-yellow" alt="Status"/>
</div>

## 📋 À propos du projet

Cette API Spring Boot permet de gérer la billetterie pour les Jeux Olympiques de Paris 2024. Elle offre des fonctionnalités de réservation, de gestion des événements sportifs et de traitement des billets pour les compétitions olympiques.

## 🛠️ Technologies utilisées

<table>
  <tr>
    <td><strong>Backend</strong></td>
    <td>
      <img src="https://img.shields.io/badge/Java-21-orange" alt="Java 21"/><br/>
      <img src="https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen" alt="Spring Boot"/><br/>
      <img src="https://img.shields.io/badge/Spring%20Security-3.3.5-brightgreen" alt="Spring Security"/>
    </td>
  </tr>
  <tr>
    <td><strong>Base de données</strong></td>
    <td>
      <img src="https://img.shields.io/badge/SQLite-3.47.0-blue" alt="SQLite"/><br/>
      <img src="https://img.shields.io/badge/MariaDB-11.4.6-blue" alt="MariaDB"/>
    </td>
  </tr>
  <tr>
    <td><strong>Outils</strong></td>
    <td>
      <img src="https://img.shields.io/badge/Lombok-1.18.34-red" alt="Lombok"/><br/>
      <img src="https://img.shields.io/badge/JWT-0.11.5-blue" alt="JWT"/><br/>
      <img src="https://img.shields.io/badge/OpenAPI-2.8.8-green" alt="OpenAPI"/><br/>
      <img src="https://img.shields.io/badge/JaCoCo-0.8.11-orange" alt="JaCoCo"/>
    </td>
  </tr>
</table>

## ⚙️ Prérequis

- JDK 21
- Maven 3.8+
- MariaDB (pour environnement de production)

## 🚀 Installation et démarrage

### Cloner le dépôt
```bash
git clone https://github.com/votreorganisation/api-billetterie-jo2024.git
cd api-billetterie-jo2024
```

### Compiler le projet
```bash
mvn clean install
```

### Exécuter l'application
```bash
mvn spring-boot:run
```

L'application sera accessible à l'adresse: http://localhost:8080

## 📖 Documentation de l'API

La documentation Swagger de l'API est disponible à l'adresse suivante une fois l'application lancée:
```
http://localhost:8080/swagger-ui.html
```

<details>
  <summary>📸 Aperçu de l'interface Swagger</summary>
  <p align="center">
    <i>Capture d'écran de l'interface Swagger disponible après lancement</i>
  </p>
</details>

## 📁 Structure du projet

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── jo
│   │           └── api
│   │               ├── config          # Configuration Spring
│   │               ├── controllers     # Contrôleurs REST
│   │               ├── exception       # Gestionnaires d'exceptions
│   │               ├── models          # Entités JPA
│   │               ├── repositories    # Repositories Spring Data
│   │               ├── security        # Configuration de sécurité
│   │               └── services        # Logique métier
│   └── resources
│       ├── application.properties      # Configuration de l'application
│       └── static                      # Ressources statiques
└── test
    └── java                            # Tests unitaires et d'intégration
```

## 💾 Base de données

L'application utilise deux systèmes de base de données:

- **SQLite** - Pour l'environnement de développement (plus facile pour votre déploiement local,
  mais interdit en production car trop risqué pour la sécurité de l'API)
  
  *Une fois l'application bien prise en main, nous vous conseillons de passer à MariaDB
  même pour l'environnement de développement*
  
- **MariaDB** - Pour l'environnement de production

La configuration de la base de données peut être modifiée dans le fichier `application.properties`.

### Exemple de configuration

```properties
# Configuration SQLite (développement)
spring.datasource.url=jdbc:sqlite:billetterie.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect

# Configuration MariaDB (production)
# spring.datasource.url=
# spring.datasource.username=
# spring.datasource.password=
# spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
# spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
```

## 🧪 Tests

Pour exécuter les tests:
```bash
mvn test
```

### Rapport de couverture

Un rapport de couverture de tests est généré avec JaCoCo après l'exécution des tests:

```bash
mvn jacoco:report
```

Le rapport est disponible dans le répertoire `target/site/jacoco/index.html`.

<div align="center">
  <img src="https://img.shields.io/badge/Coverage%20Target-60%25-success" alt="Coverage Target"/>
</div>

> La configuration JaCoCo exige une couverture minimale de 60% pour les instructions et les branches.

## 📦 Déploiement

L'application est packagée sous forme de fichier WAR et peut être déployée sur un serveur Tomcat.

### Construction du WAR
```bash
mvn clean package
```

Le fichier WAR sera généré dans le répertoire `target/api-0.0.1-SNAPSHOT.war`.

### Déploiement sur Tomcat
```bash
cp target/api-0.0.1-SNAPSHOT.war ${TOMCAT_HOME}/webapps/billetterie.war
```

### Déploiement avec Docker

```bash
# Construction de l'image
docker build -t billetterie-jo:latest .

# Exécution du conteneur
docker run -d -p 8080:8080 --name billetterie-api billetterie-jo:latest
```

## 🏆 Fonctionnalités principales

- **🎟️ Gestion des billets**
  - Création et réservation de billets
  - Validation et annulation
  - QR codes et contrôle d'accès

- **👤 Gestion des utilisateurs**
  - Authentification JWT
  - Rôles et permissions
  - Profils personnalisés

- **📊 Tableau de bord administrateur**
  - Statistiques de vente
  - Suivi des affluences
  - Rapports

## 👨‍💻 Auteurs

<div align="center">
  <strong>Développé avec ❤️ par</strong>
  <br>
  Axel Teisseire
</div>

---

<div align="center">
  <br>
  <img src="https://img.shields.io/badge/Made%20with-Spring%20Boot-brightgreen" alt="Made with Spring Boot"/>
</div>
