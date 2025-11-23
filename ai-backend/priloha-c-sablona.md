# AI Workflow Dokumentácia

**Meno:** Matej Delincak

**Dátum začiatku:** 22.11.2025

**Dátum dokončenia:** 

**Zadanie:** Backend

---

## 1. Použité AI Nástroje

Vyplň približný čas strávený s každým nástrojom:

- [x] **Claude Code:** 3 hodín  
- [x] **GitHub Copilot:** _____ hodín
- [x] **Claude.ai:** _____ hodín

**Celkový čas vývoja (priližne):** _____ hodín

[ ] ✅ Fungoval perfektne (first try)  
[ ] ⭐⭐⭐⭐ Dobré, potreboval malé úpravy  
[ ] ⭐⭐⭐ OK, potreboval viac úprav  
[ ] ⭐⭐ Slabé, musel som veľa prepísať  
[ ] ❌ Nefungoval, musel som celé prepísať

---

## 2. Zbierka Promptov

### Prompt #1: 

**Nástroj:** Claude Code 

**Kontext:** Setup projektu

**Prompt:**
```
i want to create a spring boot application, add lombok and neccesary libraries which you will need. I will give
you a list of things for rest api:

Users module:
User has following fields: id, name max length 100, email max length 100 and is unique,
password string
Create CRUD REST API for this module
Validate input DTOs. If wrong return 400

------------

Authetication module:
Login REST API
Check user credentials (email, password) and if correct return JWT token

--------------

Products module:
Product has following fields: id, name string max length 100, description string, price number >=
0, stock number >= 0, created\_at timestamp
Create CRUD REST API for this module
Validate input DTOs. If wrong return 400

-------

Orders module:
Order has following fields: id, user\_id , total number >= 0, status enum (pending, processing,
completed, expired), items schema id primary key, product\_id, quantity number > 0, price
number > 0 created\_at timestamp, updated\_at timestamp
Create CRUD REST API for this module
Validate input DTOs. The rules are in scheme

------

Additional requirements
Endpoints has to be protected with JWT Bearer token. Result of Login REST API.
Correctly handle error return states
400 Bad Request
401 Unauthorized
404 Not Found
500 Internal Server Error
Include OpenAPI/Swagger documentation
```

**Výsledok:**  
⭐⭐⭐ OK, potreboval viac úprav

**Úpravy:**

Pomocou dalsieho promptu som upravil sposob generovania JWT tokenu, pretoze metoda parserBuilder() neexistuje v novej verzii kniznice. 
Taktiez som nahradil vsetky deprecated metody novymi alternativami podla dokumentacie kniznice io.jsonwebtoken.
Taktiez som pridal Swagger dokumentaciu a spravne osetrenie chybovych stavov.


**Poznámky:**


Nezadefinoval som verziu kniznice io.jsonwebtoken v prompte, co sposobilo ze AI pouzila starsiu verziu s deprecated metodami. V buducnosti budem vzdy specifikovat verzie kniznic v prompte.

---

### Prompt #2: 
**Nástroj:** Claude Code

**Kontext:** Oprava deprecated metód v JWT generovaní

**Prompt:**
```
does not compile and you used old deprecated methods, fix the error and warnings with newer version:
cannot find symbol method parserBuilder()
io.jsonwebtoken.SignatureAlgorithm in io.jsonwebtoken has been deprecated
setClaims(java.util.Map<java.lang.String,?>) in io.jsonwebtoken.JwtBuilder has been deprecated
setSubject(java.lang.String) in io.jsonwebtoken.ClaimsMutator has been deprecated
setIssuedAt(java.util.Date) in io.jsonwebtoken.ClaimsMutator has been deprecated
setExpiration(java.util.Date) in io.jsonwebtoken.ClaimsMutator has been deprecated
signWith(java.security.Key,io.jsonwebtoken.SignatureAlgorithm) in io.jsonwebtoken.JwtBuilder has been deprecated
```
✅ Fungoval perfektne (first try)

---

### Prompt #3: 

**Nástroj:** Claude Code

**Kontext:** Oprava chybových stavov a pridanie Swagger dokumentácie

**Prompt:**
```
add proper swagger with documentaed error codes: 
Correctly handle error return states
400 Bad Request
401 Unauthorized
404 Not Found
500 Internal Server Error
```

**Výsledok:**
❌ Nefungoval, musel som celé prepísať

**Úpravy:**


Swagger dokumentacia bola slaba a chybove stavy neboli spravne osetrene. Pomocou tohoto promptu som pridal Swagger anotacie ku kazdemu endpointu, aby boli chybove stavy zdokumentovane. Taktiez som upravil exception handling v kontroleroch, aby vracali spravne HTTP status kody podla specifikacie.



**Poznámky:**


Cakal som ze to spravi poriadne hned pri prvom prompte, ale nespravil tak, tento prompt to tiez nefixol. Skusim este raz.

---

### Prompt #4: 

**Nástroj:** Claude Code

**Kontext:** Priprava Docker Compose s PostgreSQL databázou

**Prompt:**
```
create me docker compose with this app and add also a postgresql db, connect the app to it (instead of h2), and
  create a seeder for admin user so i can login
```

**Výsledok:**
✅ Fungoval perfektne (first try)

---

### Prompt #5: 

**Nástroj:** Claude Code

**Kontext:** Oprava chyboveho stavu 403 namiesto 401

**Prompt:**
```
when i am not authorized, and i call the api, i get 403 and o should get 401, based on my assignemtent
```

**Výsledok:**
✅ Fungoval perfektne (first try)

**Úpravy:**
V prvom prompte som sice poziadal o 401 Unauthorized, ale AI nastavila 403 Forbidden. Tento prompt to opravil.


**Poznámky:**
Neuvedomil som si tento detail hned na zaciatku, a explicitne som na to neupozornil.

---

### Prompt #6:

**Nástroj:** Claude Code

**Kontext:** Oprava swagger definícií a chybových stavov

**Prompt:**
```
please remove all swagger definitions and start again over, because you are returning the same error dto and you do
  not consider the right status codes, so now, create a swagger definitions so that it makes sense, when returinn 500
  for exmaple do not return bad reguest dto. Also when returnin 401, 404 or 500 return only a body that consist of one
  message which will be error. When retunrnin 400, also add validation errors (but they need to be different
  according to each endpoint)
```

**Výsledok:**
✅ Fungoval perfektne (first try)

**Úpravy:**
Prompt 3 spravil uplne hovadiny, tak som to musel cele spravit nanovo. Tentokrat som presne specifikoval ake dto sa ma vracat pri jednotlivych statusoch. Taktiez som poziadal o validacne chyby specificke pre kazdy endpoint.

**Poznámky:**
S odpovedou som uz viac spokojny. Este by sa dali zlepsit examples v swaggeri, ale to uz necham tak.

_________________________________

### Prompt #7: 

**Nástroj:** Claude Code

**Kontext:** Integracne testy pre vsetky endpointy

**Prompt:**
```
now create me 7 integration tests together for all endpoints, prioritize creating entities, login, make one to test
  authorization fail, ...
```

**Výsledok:**
⭐⭐⭐⭐ Dobré, potreboval malé úpravy

**Úpravy:**
Vytvoril si skript na data, ale dal tam pevne idcka, co sposobilo chyby pri behu testov. Povedal som mu nech pouzije automaticke generovanie idciek pomocou next_uuid() alebo hibernate sekvencie, aby sa predišlo konfliktom s existujucimi datami v db.

**Poznámky:**

_________________________________

### Prompt #8:

**Nástroj:** Claude Code

**Kontext:** Oprava idčiek v integračných testoch

**Prompt:**
```
i already have some things in db, change the id for next_uuid() or
      next(hibernate sequence) or something
```

**Výsledok:**

✅ Fungoval perfektne (first try)

_________________________________

### Prompt #9:

**Nástroj:** Github copilot

**Kontext:** Vygenerovanie commit message pre git

**Prompt:**

Použil som vbudovaný nástroj GitHub Copilot v VSCode na vygenerovanie commit message pre git.

**Výsledok:**

✅ Fungoval perfektne (first try)

---

### Prompt #10:

**Nástroj:** Github copilot

**Kontext:** Priprava na PRP

**Prompt:**
```
/init
```

**Výsledok:**

✅ Fungoval perfektne (first try)

---

### Prompt #11:

**Nástroj:** Github copilot

**Kontext:** 

**Prompt:**
```

```

**Výsledok:**

---

## 3. Problémy a Riešenia 

> 💡 **Tip:** Problémy sú cenné! Ukazujú ako riešiš problémy s AI.

### Problém #1: _________________________________

**Čo sa stalo:**
```
[Detailný popis problému - čo nefungovalo? Aká bola chyba?]
```

**Prečo to vzniklo:**
```
[Tvoja analýza - prečo AI toto vygeneroval? Čo bolo v prompte zlé?]
```

**Ako som to vyriešil:**
```
[Krok za krokom - čo si urobil? Upravil prompt? Prepísal kód? Použil iný nástroj?]
```

**Čo som sa naučil:**
```
[Konkrétny learning pre budúcnosť - čo budeš robiť inak?]
```

**Screenshot / Kód:** [ ] Priložený

---

### Problém #2: _________________________________

**Čo sa stalo:**
```
```

**Prečo:**
```
```

**Riešenie:**
```
```

**Learning:**
```
```

## 4. Kľúčové Poznatky

### 4.1 Čo fungovalo výborne

**1.** 
```
[Príklad: Claude Code pre OAuth - fungoval first try, zero problémov]
```

**2.** 
```
```

**3.** 
```
```

**[ Pridaj viac ak chceš ]**

---

### 4.2 Čo bolo náročné

**1.** 
```
[Príklad: Figma MCP spacing - často o 4-8px vedľa, musel som manuálne opravovať]
```

**2.** 
```
```

**3.** 
```
```

---

### 4.3 Best Practices ktoré som objavil

**1.** 
```
[Príklad: Vždy špecifikuj verziu knižnice v prompte - "NextAuth.js v5"]
```

**2.** 
```
```

**3.** 
```
```

**4.** 
```
```

**5.** 
```
```

---

### 4.4 Moje Top 3 Tipy Pre Ostatných

**Tip #1:**
```
[Konkrétny, actionable tip]
```

**Tip #2:**
```
```

**Tip #3:**
```
```

---

## 6. Reflexia a Závery

### 6.1 Efektivita AI nástrojov

**Ktorý nástroj bol najužitočnejší?** _________________________________

**Prečo?**
```
```

**Ktorý nástroj bol najmenej užitočný?** _________________________________

**Prečo?**
```
```

---

### 6.2 Najväčšie prekvapenie
```
[Čo ťa najviac prekvapilo pri práci s AI?]
```

---

### 6.3 Najväčšia frustrácia
```
[Čo bolo najfrustrujúcejšie?]
```

---

### 6.4 Najväčší "AHA!" moment
```
[Kedy ti došlo niečo dôležité o AI alebo o developmente?]
```

---

### 6.5 Čo by som urobil inak
```
[Keby si začínal znova, čo by si zmenil?]
```

### 6.6 Hlavný odkaz pre ostatných
```
[Keby si mal povedať jednu vec kolegom o AI development, čo by to bylo?]
```
