# Kundenpflege App

Eine React-basierte Web-Applikation zur Kundenverwaltung mit Master-Detail-Ansicht.

## Tech Stack

- **React 19** - UI Framework
- **TypeScript** - Type Safety
- **Vite** - Build Tool
- **Tanstack Query** - Server State Management
- **Tanstack Table** - Tabellen-Komponente
- **Tanstack Forms** - Formular-Management
- **Zod** - Schema-Validierung
- **React Router** - Client-seitiges Routing

## Features

- **Kundenliste (Master-Ansicht)**: Übersicht aller Kunden mit Sortierung
- **Kundendetails (Detail-Ansicht)**: Detailansicht eines einzelnen Kunden
- **Kunde anlegen**: Formular zum Erstellen neuer Kunden
- **Adressen bearbeiten**: Rechnungs- und Lieferadresse hinzufügen/bearbeiten
- **Validierung**: Client-seitige Validierung mit Zod

## Voraussetzungen

Das Backend (customer-service) muss auf `http://localhost:8080` laufen.

## Installation & Start

```bash
# Dependencies installieren
npm install

# Entwicklungsserver starten
npm run dev

# Build für Produktion
npm run build

# Build-Preview
npm run preview
```

## Umgebungsvariablen

`.env` Datei:

```
VITE_API_URL=http://localhost:8080/api
```

## API-Endpunkte

Die App nutzt folgende Backend-Endpunkte:

- `GET /api/customers/` - Alle Kunden abrufen
- `POST /api/customers/` - Neuen Kunden erstellen
- `GET /api/customers/{customerNumber}` - Einzelnen Kunden abrufen
- `PUT /api/customers/{customerNumber}/billing-address` - Rechnungsadresse aktualisieren
- `PUT /api/customers/{customerNumber}/delivery-address` - Lieferadresse aktualisieren

## Projektstruktur

```
src/
├── api/              # API-Client und React Query Hooks
├── components/       # Wiederverwendbare Komponenten (Formulare)
├── pages/            # Seiten-Komponenten (Routen)
├── types/            # TypeScript-Typen und Zod-Schemas
├── App.tsx           # Haupt-App-Komponente mit Routing
├── App.css           # Globale Styles
└── main.tsx          # Entry Point
```

## Entwickelt mit

Claude Code - AI-gestützte Entwicklung
