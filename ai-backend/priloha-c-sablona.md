# AI Workflow Dokumentácia

**Meno:** Matej Delincak

**Dátum začiatku:** 22.11.2025

**Dátum dokončenia:** 23.11.2025

**Zadanie:** Backend

---

## 1. Použité AI Nástroje

Vyplň približný čas strávený s každým nástrojom:

- [x] **Claude Code:** 8 hodín  
- [x] **GitHub Copilot:** 0.4 hodín

**Celkový čas vývoja (priližne):** 8.5 hodín

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

**Nástroj:** Claude code

**Kontext:** Priprava na PRP

**Prompt:**
```
/init
```

**Výsledok:**

✅ Fungoval perfektne (first try)

---

### Prompt #11:

**Nástroj:** Claude code

**Kontext:** Generate prp for feature

**Prompt:**
```
/generate-prp INITIAL.md

## FEATURE:

Use some messaging service Kafka. Update docker compose file so this service can be created inside docker.
Add event bus into the project so the messages/events can be sent/published.

Order creation handling:
 - When the order is created the OrderCreated event has to be published
 - There will be handling of this event which:
   - Update order status: pending → processing
   - Simulate payment processing (5 second delay)
   - Update order status for 50% of cases to completed and publish OrderCompleted event
   - In another 50% of cases do not change the status
   
Order expiration handling:
   - Add recursive job which will run every 60 seconds
   - The job find orders with status='processing' older than 10 minutes and update the status to
   'expired'
   - Publish OrderExpired event

Notifications handling:
   - Create new notifications table and add upgrade script/code
   - When the OrderCompleted event is published
     - Send email notification (fake/mock - log to console)
     - Save notification to database (audit trail)
   - When the OrderExpired event is published
     - Save notification to database (audit trail)
     - 
## EXAMPLES:

Expected Flow:
1. User creates order via POST /api/orders
2. Order saved to DB with status='pending'
3. OrderCreated event published
4. OrderProcessor handles event asynchronously:
   Updates status to 'processing'
   Simulates payment (5 sec delay)
   Updates status to 'completed'
5. OrderCompleted event published
6. Notifier handles event:
   Logs fake email to console
   Saves notification to DB
7. CRON job runs every 60s:
   Finds pending orders older than 10 minutes
   Updates them to 'expired

## DOCUMENTATION:

https://kafka.apache.org/
Properly read README.md, CLUADE.md and API_DOCUMENTATION.md

## OTHER CONSIDERATIONS:

- Use existing project patterns for database access, error handling, logging, etc.
- Create some new file for tests which will test this new features with kafka, at least 4 test
```

**Výsledok:**
✅ Fungoval perfektne (first try)

**Úpravy:**
8.5/10 - scoring, ale iba pre nejake testy znizene score bolo, za mna ok. Aj vysledny md subor bol velmi kvalitny.

**Poznámky:**

---

### Prompt #11:

**Nástroj:** Claude code

**Kontext:** Execute prp for feature

**Prompt:**
```
/execute-prp PRPs/kafka-event-driven-orders.md
```

**Výsledok:**
⭐⭐⭐⭐ Dobré, potreboval malé úpravy

**Úpravy:**

Implementacia bola dobra ale testy nefungovali tak idem skumat dalej. Viem ze k druhej casti netreba testy.

**Poznámky:**

---

### Prompt #12:

**Nástroj:** Claude code

**Kontext:** Tests not working

**Prompt:**
```
no stop testing, and build docker compose, there are some errors fix them and
      when docker compose is running then you can try tests
```

**Výsledok:**

❌ Nefungoval, musel som celé prepísať

**Úpravy:**

Nedokazal opravit preco testy nejdu.

**Poznámky:**

Zistil som ze zookeeper nejde spustit, tak som to riesil samostatne v dalsom prompte.

---

### Prompt #13:

**Nástroj:** Github copilot

**Kontext:** Zookeper issue

**Prompt:**
```
when i try to open zookepper it gives me this error: ecommerce-zookeeper  | java.io.IOException: Len error. A message from /172.22.0.1:48302 with advertised length of 1195725856 is either a malformed message or too large to process (length is greater than jute.maxbuffer=1048575)
```

**Výsledok:**

❌ Nefungoval, musel som celé prepísať

**Úpravy:**

Spravil nejaky novy parameter, chyba nezmizla.

**Poznámky:**

Skusil som github copilota.

---

### Prompt #14:

**Nástroj:** Github copilot

**Kontext:** Zookeper issue

**Prompt:**
```
 What is zookeper used for...
```

**Výsledok:**

✅ Fungoval perfektne (first try)

**Poznámky:**

Myslel som ze zookeper je nejaky gui nastroj na zobrazenie dat v kafka, ale je to nieco ine. :) Tym padom vsetko ide, iba testy zatial nie.

---

## 3. Problémy a Riešenia 

### Problém #1: Deprecated metódy v JWT generovaní

**Čo sa stalo:**

Claude Code vygeneroval kód na generovanie JWT tokenu pomocou zastaralých metód, ktoré už nie sú podporované v najnovšej verzii knižnice io.jsonwebtoken. Konkrétne použil metódu parserBuilder(), ktorá bola odstránená.

**Prečo to vzniklo:**

Nezadefinoval som verziu knižnice io.jsonwebtoken v prompte, čo spôsobilo, že AI použila staršiu verziu s deprecated metódami.

**Ako som to vyriešil:**

Napisal som ďalší prompt, v ktorom som požiadal o opravu kódu tak, aby používal aktuálne metódy podľa najnovšej dokumentácie knižnice. Claude Code následne vygeneroval opravený kód, ktorý už neobsahoval deprecated metódy.

**Čo som sa naučil:**

Treba vždy špecifikovať verzie knižníc v prompte, aby sa predišlo použitiu zastaralých alebo nekompatibilných metód.

---

### Problém #2: Slaba swagger dokumentácia a chybové stavy

**Čo sa stalo:**

Claude Code vygeneroval Swagger dokumentáciu a chybové stavy, ktoré neboli správne ošetrené a dokumentované. Chybové stavy neboli konzistentné s požiadavkami a dokumentácia bola nedostatočná.

**Prečo to vzniklo:**

AI nepochopila presne moje požiadavky na chybové stavy a spôsob dokumentácie v Swaggeri. Pravdepodobne som nebol dostatočne konkrétny v pôvodnom prompte a nezdoraznil som všetky detaily.

**Ako som to vyriešil:**

Dalsimi dvoma promptami som požiadal o odstránenie existujúcich Swagger definícií a ich opätovné vytvorenie s dôrazom na správne chybové stavy a špecifické DTO pre každý stav. Taktiež som požiadal o validácie špecifické pre každý endpoint.

**Čo som sa naučil:**

Je dôležité byť veľmi konkrétny a detailný v promptoch, najmä pri požiadavkách na dokumentáciu a chybové stavy. Niekedy je potrebné iterovať a opraviť generovaný kód viackrát, aby sa dosiahla požadovaná kvalita. Asi som bol len príliš naročný na kvalitu.

---

### Problém #3: Testy k druhej časti Kafka integrácie nefungovali

**Čo sa stalo:**

Claude code ma sice v PRP informoval, ze tam mozu byt chyby v dosledku async veci a nahodnom spravani statu objednavok, ale testy nefungovali spravne a ja som nevedel preco.
Problem bol ale v tom, ze testy bezali na H2 databaze, ale v docker compose som mal nastavenu Postgresql databazu pre aplikaciu. Testy preto nevideli spravne data.

**Prečo to vzniklo:**

Na toto by ai asi neprisla, asi 20 minut skusal rozne veci, ale nakoniec som to vyriesil sam.

**Ako som to vyriešil:**

Pozrel som sa na kod a hned ma napadlo ze to moze byt databazou. Upravil som konfiguraciu testov aby pouzivali postgresql databazu v docker compose. A testy az na dve prebehli.
U tych dvoch som musel upravit casovanie, pretoze nedostatocne dlho cakal a test nevysiel. Po uprave casov testy prebehli.

**Čo som sa naučil:**

AI zatial nevie vyriesit vsetky problemy, hlavne tie suvisiace s konfiguraciou a prostredim. Uz sa mi to stalo par krat aj v praxi. Co ale dokaze vyborne je napisat kde a co hladat.
Mozno by to AI zvladlo, ale trvalo by to velmi dlho. (Pravdepodobne by to aj dost stalo).

## 4. Kľúčové Poznatky

### 4.1 Čo fungovalo výborne

**1.** 
Claude code vygeneroval REST API s CRUD a JWT autentifikaciou na prvý pokus a bol som prekvapeny ako dobre to spravil a hlavne rychlo.

**2.** 
Taktiez napojenie na postgresql databazu a vytvorenie docker compose s db a seedrom pre admin usera bolo bez problemov.

**3.** 
Testy pre prvu cast generoval velmi kvalitne a hned zbehli.

**4.**
Integracia Kafka a generovanie PRP bolo velmi kvalitne a podrobne.

**5.**
Dokonca aj generovanie commit message pomocou github copilota fungovalo bez problemov.

**6.**
Vygenerovanie druhej casti, co sa tyka kodu ako takeho tak bez chyby akurat ja som nerozumel casti systemu ako ma fungovat. Po rucnom otestovani, vsetko OK.

---

### 4.2 Čo bolo náročné

**1.** 
Presvedcit AI ako ma vyzerat Swagger dokumentacia a chybove stavy. Trvalo to viacerymi iteraciami.

**2.** 
Co som si vsimol tak niekedy je tazke zastavit claude code a stale sa ma pytal na dalsie veci, aj ked som mu povedal ze uz nic viac nepotrebujem.
Copilot taky nie je a ked dam stop tak prestane.

### 4.3 Best Practices ktoré som objavil

**1.** 
Treba byt konkretny vo verziach kniznic, pretoze AI rado spadne do starsej verzie kde su deprecated metody.

**2.** 
Vyuzivat viacej PRP pre komplexnejsie veci, lebo AI vie velmi dobre vygenerovat kvalitny plan a potom ho aj vykonat.

**3.** 
Urcite ale urcite viacej rozdelovat zadanie do mensich casti, lebo AI ma problem s udrzanim kontextu pri vacsich zadaniach. 
To som si vsimol aj tym ze uz dlhsie robim s AI a pri velkych ulohach zacne halucinovat.

---

### 4.4 Moje Top 3 Tipy Pre Ostatných

**Tip #1:**
Pouzivaj PRP pre komplexnejsie veci.

**Tip #2:**
Rozdeluj velke ulohy do mensich casti.

**Tip #3:**
Po kazdej feature cisti kontext.

---

## 6. Reflexia a Závery

### 6.1 Efektivita AI nástrojov

**Ktorý nástroj bol najužitočnejší?** Claude Code

**Prečo?**

Github copilot pouzivam uz nejaky ten mesiac tak to viem porovnat. Claude code vie lepsie a lahsie ziskat kontext. Copilot ma s tymto problem, a je zdlhavejsie a tazsie mi ten context dat.

**Ktorý nástroj bol najmenej užitočný?** Chat GPT

**Prečo?**

Opat problem s kontextom a neni tam ta integracia na IDE.

---

### 6.2 Najväčšie prekvapenie
Ze vie pekne pri malom prompte vygenerovat kvalitny plan a potom ho aj vykonat.

---

### 6.3 Najväčšia frustrácia
Ked su nejake konfiguracne problemy alebo network issues, AI si s tym nevie rady a skusa do hlupa.

---

### 6.4 Najväčší "AHA!" moment
Je lepsie kvalitnejsie specifikovat prompt a mat viac mensich promptov ako jeden velky. Doteraz som vzdy robil kratke prompty a nie vzdy som dostal co som chcel. Lepsie bolo vzdy pockat.

---

### 6.5 Čo by som urobil inak
Urcite by som viacej rozpisal verziu kniznic a presnejsie specifikoval detaily ohladom implementacie.

---

### 6.6 Hlavný odkaz pre ostatných
Lepsie je napisat velky kvalitny prompt a pockat dlhsie ako pisat kratke prompty vela razy.
