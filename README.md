# README.md



## Om prosjektet

Dette er en Android-applikasjon utviklet i Kotlin med Jetpack Compose.
Appen gir brukeren informasjon om solenergipotensial basert på valgt bolig, strømpriser og værdata.
Brukeren søker bolig på et interaktivt kart, og appen henter relevant data fra eksterne API-er.

Prosjektet er laget som del av emnet IN2000 ved Universitetet i Oslo.

## Hvordan kjøre appen

1. Åpne prosjektet i Android Studio Code.
2. Bygg prosjektet via Build > Make Project eller med hurtigtasten `Ctrl+F9`.
3. Kjør appen på emulator eller fysisk Android-enhet (API 26 eller høyere).
4. Appen trenger kun tilgang til internett. Den ber ikke om lokasjonstillatelse.

## API-nivå

- **minSdk**: 26 (Android 8.0)
- **targetSdk**: 35 (Android 14)
- **compileSdk**: 35

Valgene er gjort for å støtte et bredt spekter av enheter, samtidig som vi kan bruke moderne komponenter.

## Avhengigheter

Vi bruker **Gradle Version Catalog** via `libs.versions.toml` for å håndtere alle biblioteker. Dette gjør det enklere å holde versjoner konsistente og gir bedre oversikt i større prosjekter.

## Brukte biblioteker


| Bibliotek           | Bruk                                             | Dekket i kurs   |
|---------------------|--------------------------------------------------|-----------------|
| Jetpack Compose     | Brukergrensesnitt                                | Ja              |
| Room                | Lokal database                                   | Ja              |
| Dagger Hilt         | Dependency Injection                             | Ja              |
| Ktor                | API-kall og JSON-deserialisering                 | Ja              |
| Navigation Compose  | Navigasjon mellom skjermer                       | Ja              |
| MapLibre            | Kartvisning med markører                         | Nei             |
| VICO                | Grafer for solproduksjon og strømpriser          | Nei             |
| MockK               | Enhetstesting og mocking                         | Delvis          |

### Kort om MapLibre og VICO

- **MapLibre** er et åpen kildekode-kartverktøy brukt til å vise og velge boliger på kartet.
- **VICO** er et grafbibliotek brukt til å vise strømpriser og produksjonsdata på en tydelig måte i ProdScreen.

## Kontakt og videre arbeid

Se `ARCHITECTURE.md` for teknisk oversikt og anbefalinger ved videreutvikling.
