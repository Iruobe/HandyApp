# CLAUDE.md — HandyApp Project Rules

## File Safety
- Never delete any file without explicit user approval first
- Always show planned changes before applying them and wait for confirmation
- Work through tasks one at a time; wait for confirmation before moving to the next

## Language
- All code must remain in Java — do not introduce Kotlin under any circumstances

## Currency
- Always use £ (British pounds) for all currency display
- Never use $ or any other currency symbol

## Design System
- Always apply 12dp corner radius to all UI elements — buttons, inputs, cards, containers. No exceptions
- Always reference colour values from colors.xml — never hardcode colours in layouts or Java files
- Always reference dimension values from dimens.xml — never hardcode sizes

## Firebase / Forms
- Always add input validation to any form before submitting data to Firebase
- Never expose API keys, credentials, or sensitive config values in code
- Always write new user data to the "users" Firestore collection with a "role" field
- Always use the Firebase Auth UID as the Firestore document ID

## Build
- Never modify build.gradle.kts without asking first
