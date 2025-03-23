# Návod na instalaci a spuštění aplikace lokálně

## Požadavky
- Java 17 nebo novější
- Maven
- Node.js 16 nebo novější
- npm

## Backend (Spring Boot aplikace)

1. **Přejděte do složky s backend aplikací:**
   ```
   cd shop
   ```

2. **Zkompilujte a spusťte aplikaci pomocí Maven:**
   ```
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
   
   Alternativně můžete použít:
   ```
   ./mvnw clean verify
   java -jar target/shop-0.0.1-SNAPSHOT.jar
   ```

3. **Po úspěšném spuštění bude API dostupné na:**
   - http://localhost:8080
   - Swagger dokumentace: http://localhost:8080/swagger-ui/index.html

## Frontend (React/Vite aplikace)

1. **Přejděte do složky s frontend aplikací:**
   ```
   cd admin-view
   ```

2. **Nainstalujte potřebné závislosti:**
   ```
   npm install
   ```

3. **Spusťte vývojový server:**
   ```
   npm run dev -- --port 3000
   ```

4. **Po úspěšném spuštění bude frontend dostupný na:**
   - http://localhost:3000

## Poznámky k použití

- **Registrace uživatele:** Vytvořte si účet se jménem a heslem (minimálně 5 znaků)
- **Přihlášení:** Použijte vytvořené přihlašovací údaje
- **Správa produktů:** Po přihlášení můžete spravovat produkty, včetně přidávání, úprav a deaktivace
- **Objednávky:** Systém umožňuje vytváření a správu objednávek

## Řešení problémů

- Pokud port 8080 není dostupný, můžete změnit port backend aplikace v souboru `shop/src/main/resources/application.properties`
- Pokud port 3000 není dostupný, můžete spustit frontend na jiném portu: `npm run dev -- --port 3001`
- V případě problémů s databází zkontrolujte konfiguraci v `application.properties`

## Databáze

Aplikace používá H2 in-memory databázi, která se resetuje při každém restartu. Přístup k H2 konzoli:
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:shopdb
- Uživatel: sa
- Heslo: password 