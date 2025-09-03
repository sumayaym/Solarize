# Solarize 

## Om prosjektet
**Solarize** er en Android-app utviklet i **Kotlin** med **Jetpack Compose**.  
Appen estimerer solenergipotensialet til en bolig basert på værdata, strømpriser og takflater.  

- Brukeren kan søke etter boliger på et interaktivt kart.  
- Hver bolig kan ha flere takflater, som lagres lokalt med **Room Database**.  
- Data hentes fra eksterne API-er og visualiseres gjennom grafer og kart.  

Appen ble utviklet som del av **IN2000 – Software Engineering med prosjektarbeid** ved Universitetet i Oslo (våren 2025).  

---

## Teknologistakk
- **Kotlin + Jetpack Compose** – moderne Android-utvikling  
- **Room** – lokal database for boliger og takflater  
- **Ktor** – API-kall og JSON-deserialisering  
- **Dagger Hilt** – dependency injection  
- **MapLibre** – interaktiv kartvisning  
- **VICO** – grafer for solproduksjon og strømpriser  

---

## Hvordan kjøre appen
1. Åpne prosjektet i **Android Studio**.  
2. Bygg og kjør på emulator eller fysisk enhet (API 26+).  
3. Appen krever kun internett-tilgang.  
