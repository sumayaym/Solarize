# ARCHITECTURE.md

## Arkitekturvalg

Vi følger MVVM (Model-View-ViewModel) for å skille mellom datahåndtering og UI-logikk.

---

## ☀️ PVGIS-funksjonalitet

- **Repository**: `PVGISRepository.kt` – bruker datasourcen, implementerer cache
- **DataSource**: `PVGISDataSource.kt` – henter data fra PVGIS med Ktor
- **ViewModel**: `SolarViewModel.kt` – eksponerer data for UI

I PVGIS-modulen er det bevisst valgt eksplisitt `if-else`-kontrollflyt fremfor Kotlin-funksjoner som `.run {}`, for å gjøre logikken tydelig og lett forståelig under utvikling og testing.
