# Setup Application

## 1. Prerequisites
- Java 11 (or higher)
- Maven

## 2. Run the Application
You do **not** need to install any database. The application now uses an embedded H2 database which will be automatically created in the `./data` folder.

### Step 1: Initialize Database (First Run Only)
Run the following command to create tables and insert default data:
```bash
mvn exec:java -Dexec.mainClass="Utilis.InitBaseDonnee"
```

### Step 2: Run Request
```bash
mvn javafx:run
```
OR
```bash
mvn exec:java
```

## Troubleshooting
- If you encounter build errors, try running `mvn clean install` first.
- If data is missing, check if the `./data` directory exists and has write permissions.
