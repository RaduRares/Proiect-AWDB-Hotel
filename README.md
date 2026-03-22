# Hotel Management System
Construită cu Spring Boot, Maven , PostgresSQL 

Echipa va fi formata din:
    1.Costache Alexandra Grabriela
    2.Radu Rareș-Andrei

Descriere proiect:
    -Gestionarea hotelurilor, tipurilor de camere și angajaților
    -Înregistrarea oaspeților și crearea rezervărilor
    -Adăugarea serviciilor extra la rezervări (mic dejun, spa, transfer etc.)
    -Generarea automată a facturilor la check-out
    -Gestionarea furnizorilor pentru fiecare hotel
    -Autentificare și autorizare bazată pe roluri (ADMIN, RECEPTIONER, OASPETE)

Entități 

| Entitate           | Câmpuri stabilite                                                              |
|--------------------|--------------------------------------------------------------------------------|
| `hotel`            | id, nume, stele, adresa, data_infiintare                                       |
| `tip_camera`       | id, nume, pret, hotel_id                                                       |
| `oaspete`          | id, nume, prenume, email, telefon, data_inregistrare                           |
| `angajat`          | id, nume_prenume, functie, salariu, data_angajare, hotel_id                    |
| `furnizor`         | id, nume, email, tip_furnizor                                                  |
| `hotel_furnizor`   | hotel_id, furnizor_id *(tabel de legătură ManyToMany)*                         |
| `rezervare`        | id, check_in, check_out, status, creat_la, hotel_id, oaspete_id, tip_camera_id |
| `serviciu`         | id, nume, cost                                                                 |
| `rezervare_serviciu` | id, cantitate, data_folosinta, rezervare_id, serviciu_id                       |
| `factură`          | id, numar_factura, suma_totala, emisa_la, status_plata, rezervare_id           |
| `user`            |                                                                                |



Diagrama ER
```
Hotel (1) ──────────── (M) Tipuri_Camera
Hotel (1) ──────────── (M) Angajat
Hotel (1) ──────────── (M) Rezervar
Hotel (M) ──────────── (M) Furnizor        ← @ManyToMany
Oaspete  (1) ──────────── (M) Rezervare
TipCamera(1) ──────────── (M) Rezervare
Rezervare(1) ──────────── (1) Factura           ← @OneToOne
Rezervare(1) ──────────── (M) Rezervare_serviciu
Serviciu (1) ──────────── (M) Rezervare_serviciu
```
Tipuri de relații acoperite

- `@OneToOne` — Rezervare → Factură
- `@OneToMany` / `@ManyToOne` — Hotel → Camere, Hotel → Angajați, Hotel → Rezervări, Oaspete → Rezervări
- `@ManyToMany` — Hotel ↔ Furnizori, User ↔ Roluri


## USER — autentificare Spring Security

Cookie 1: JSESSIONID

- session cookie, generat automat de Spring
- expiră la închiderea browserului sau după 30 min inactivitate
- NU se stochează în DB (e în memoria serverului)

Cookie 2: remember-me
- persistent cookie, 14 zile
- setat doar dacă userul bifează "Ține-mă minte" la login
- valoarea hash-uită este stocată în tabelul persistent_logins
- la fiecare request, Spring rotește automat tokenul (securitate)

##  Setup & Rulare

###  Programe necesare 

- Java 17+
- Maven 3.9+
- PostgreSQL 15+ 

Clonează repository-ul

git clone https://github.com/RaduRares/Proiect-AWDB-Hotel
cd hotel-management

!!!! INAINTE DE FIECARE PUSH, A NU SE UITA SA SE FACA PULL PENTRU A MINIMIZA MERGE CONFLICTS !!!!!!
## Structura proiectului
```

hotel-management/
├── src/
│   ├── main/
│   │   ├── java/com/hotel/
│   │   │   ├── model/              # Entități JPA (11 clase)
│   │   │   ├── repository/         # Spring Data JPA repositories
│   │   │   ├── service/            # Business logic
│   │   │   ├── controller/         # REST Controllers
│   │   │   ├── security/           # Spring Security config
│   │   │   ├── exception/          # Exception handling
│   │   │   └── HotelManagementApplication.java
│   │   └── resources/
│   │      
│   │      
│   │      
│   │      
│   │       
│   └── test/
│       └── java/com/hotel/
│           ├── service/            # Unit tests 
│           └── integration/        # Integration tests
├── logs/
│   ├── hotel-app.log               # Log general
│   └── hotel-errors.log            # Doar erori
├── pom.xml
├── .gitignore
└── README.md

```

---