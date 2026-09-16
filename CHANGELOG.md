# 🧩 KongQuery – Changelog

## v26.4.1
- **New:** Suppress false-positive SQL inspections in JS/TS template literals
    - Automatically hides syntax errors like `expected, got 'VALUES'` and `Unable to resolve table 'VALUES'` caused by `${...}` interpolations
    - Plain SQL literals (without interpolations) still get full syntax checking and highlighting

---

## v26.4.0
- **New:** JavaScript / TypeScript support
  - Formats SQL inside template literals (backtick strings) in `.js`, `.jsx`, `.ts`, `.tsx`, `.mjs`, `.cjs` files
  - Preserves `${...}` interpolations while formatting (e.g. pg / node-postgres placeholders)
  - Available via `Ctrl+Alt+L` (⌘+⌥+L on macOS) and right-click → "Format SQL Query"
  - Context menu item only appears when the file contains SQL in template literals
- **Enhanced:** Projection Interface Generation now works in JS/TS files — select SQL and press Alt+Shift+P (⌥+⇧+P on macOS)

---

## v26.3.1
- **New:** Change ICON
  - Minor changes to the icon
- **New:** Complete SQL formatter for Spring Data JPA `@Query` annotations
  - Automatically detects and formats SQL queries inside `@Query` and `@NativeQuery` annotations
  - Supports all major SQL dialects (MySQL, PostgreSQL, Oracle, SQL Server, etc.)
  - Preserves JPA parameters and placeholders (`:param`, `?1`, etc.)
  - Multiple access methods: keyboard shortcuts, context menu, and automatic formatting
- **New:** DTO Constructor Support
  - Smart formatting for `SELECT new ClassName(field1, field2, ...)` syntax
  - Proper indentation and alignment for complex DTO constructors
- **New:** Projection Interface Generation
  - Generate Java projection interfaces from SQL queries (Alt+Shift+P / ⌥+⇧+P)
  - Supports both DTO constructors and AS aliases field extraction
  - Creates proper getter methods automatically
- **New:** Multiple Integration Methods
  - Keyboard shortcuts: `Ctrl+Alt+L` (Windows/Linux) or `⌘+⌥+L` (macOS)
  - Context menu: Right-click → "Format SQL Query"
  - Automatic formatting during save and document commit
  - Works with standalone `.sql` files as well
- **New:** Enhanced SQL Syntax Highlighting
  - Language injection support for both `@Query` and `@NativeQuery` annotations
  - Enables SQL code completion and IntelliSense inside annotations

---
