# ARCHITECTURE.md

## Hensikt

Dette dokumentet er laget for deg som skal sette deg inn i, vedlikeholde eller videreutvikle appen.
Her beskrives den overordnede arkitekturen vi har valgt, prinsippene vi har fulgt, og teknologiene vi har brukt.
Målet er å gjøre det enkelt for deg å forstå hvordan prosjektet henger sammen, og hvordan du bør jobbe videre for å holde kodebasen ryddig og robust.

---

## Teknologi og API-nivå

Appen er bygget i Kotlin for Android ved bruk av Jetpack Compose. Vi benytter Android API-nivå 26 som minimum (Android 8.0), og compile/target SDK 35 (Android 14). 
Vi valgte disse nivåene fordi API 26 dekker de fleste brukere, samtidig som vi får tilgang til moderne komponenter gjennom API 35.

Vi bruker en rekke biblioteker og verktøy:

- **Jetpack Compose** – for UI
- **Room** – for lokal datalagring
- **Dagger Hilt** – for dependency injection
- **Ktor** – for API-kall
- **MapLibre** – for kartvisning
- **VICO** – for visning av grafer (f.eks. produksjonsdata)
- **Navigation Compose** – for skjermnavigasjon

---

## Overordnet arkitektur

Appen er strukturert etter **MVVM (Model-View-ViewModel)**-mønsteret:

- **Model**: Datahåndtering, API-kall og database via `DataRepository` og DAO-er.
- **ViewModel**: Inneholder logikk og tilstandshåndtering for hver skjerm. Gir data til UI og reagerer på brukerhandlinger.
- **View (Compose UI)**: Presentasjonslaget. Bygd opp av gjenbrukbare komponenter og strukturerte skjermer.

Vi benytter også **trunk-based development** i Git og holder `main`-branchen stabil. Nye funksjoner utvikles i egne `feature/`-brancher og merges etter godkjenning.

---

## Objektorienterte prinsipper

### Lav kobling

Vi har sørget for lav kobling mellom komponenter. `DataRepository` har kun ansvar for å hente og lagre data, og UI-lagene forholder seg til ViewModels, ikke direkte til datakilder eller API.

### Høy kohesjon

Hver klasse og komponent har én klart definert oppgave. Eksempel: `FrostDao` håndterer kun lagring av værdata, og `ProductionViewModel` håndterer kun visningen av produksjonsdata.

---

## Viktige arkitekturvalg

### Shared ViewModel

Vi bruker en delt `SharedHomeViewModel` for å håndtere valgt bolig på tvers av skjermer. 
Dette gjør det mulig å synkronisere state (f.eks. hvilken bolig som er valgt) mellom f.eks. kart- og produksjonsskjermene.

### Komponentstruktur i UI

UI-en er delt opp i **gjenbrukbare komponenter**, organisert i undermapper som `MapComponents`, `ProductionComponents`, `UserComponents` osv. Dette gjør det:

- Enkelt å lese og forstå skjermfilene (de består hovedsakelig av høynivå-komponenter).
- Lett for andre utviklere å gjenbruke komponenter på tvers av skjermer.

---

## Datatilgang og lokal lagring

All data hentes og lagres gjennom `DataRepository`, som kaller API-er (Ktor) og lagrer resultater i Room-tabeller.

Vi bruker Room med auto-genererte ID-er som primary key, og knytter alle datasett til en `homeId` for enkel spørring og oppdatering. Dette unngår problemer med avrundingsfeil som kan oppstå ved bruk av lat/lon som nøkkel.

---

## Bruk av grafvisning (VICO)

I **ProductionScreen** benytter vi **VICO**-biblioteket for å vise interaktive grafer over strømpris og solproduksjon. Dette gir en moderne og responsiv grafopplevelse og gjør det enklere å tolke historiske og predikerte data visuelt.

---

## Avslutning

Appen er bygget for å være ryddig, skalerbar og lett å videreutvikle. Vi anbefaler at videre arbeid følger samme arkitektur og prinsipper for å bevare struktur og kvalitet i koden.

