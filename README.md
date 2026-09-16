
# 🦍 KongQuery — JPA @Query Formatter & Projection Generator for IntelliJ IDEA

> A powerful IntelliJ plugin that automatically formats SQL queries inside Spring Data JPA `@Query` annotations and generates Java projection interfaces.

---

## ✨ Overview

**KongQuery** helps you keep your inline SQL queries clean, readable, and beautifully formatted —
directly inside your IntelliJ editor. It detects any SQL written in your JPA repository methods and applies smart formatting rules.

Ideal for Spring developers who often write queries like this:

```java
@Query("SELECT u FROM User u WHERE u.email LIKE %:email% AND u.active = true ORDER BY u.createdDate DESC")
List<User> findByEmail(@Param("email") String email);
```

With KongQuery, it becomes instantly readable:

```java
@Query("""
    SELECT u
    FROM User u
    WHERE u.email LIKE %:email%
      AND u.active = true
    ORDER BY u.createdDate DESC
""")
List<User> findByEmail(@Param("email") String email);
```

### 🏗️ DTO Constructor Support & Projection Generation

Generate Java projection interfaces directly from DTO constructors and SQL queries!

```java
@Query("""
    SELECT new com.example.UserDto(
        u.id,
        u.username,
        u.email,
        u.createdAt
    )
    FROM User u
    WHERE u.active = true
""")
List<UserDto> findActiveUsers();
```

With KongQuery's **Generate Projection Interface** (Alt+Shift+P on Windows/Linux, ⌥+⇧+P on macOS), it creates:

```java
public interface UserDto {
    Object getId();
    Object getUsername();
    Object getEmail();
    Object getCreatedAt();
}
```

---

## ⚙️ Features

- 🧠 **Auto-detects** SQL inside `@Query`, `@NativeQuery`, and repository methods
- 💅 **Formats & beautifies** inline SQL with consistent indentation and alignment
- 🏗️ **DTO Constructor Support** - Smart formatting for `SELECT new ClassName(...)` syntax
- 🎯 **Projection Interface Generation** - Generate Java interfaces from SQL queries (Alt+Shift+P / ⌥+⇧+P)
- 🗄️ **Supports multiple dialects** — MySQL, PostgreSQL, Oracle, SQL Server, etc.
- ⚡ **Preserves parameters and placeholders** (`:param`, `?1`, etc.)
- 🌱 **Integrates seamlessly** with Spring Data JPA projects
- 🧩 **Multiple access methods** - Keyboard shortcuts, context menu, and automatic formatting
- 📝 **Smart field extraction** - Handles both `AS` aliases and DTO constructor fields
- 🟨 **JavaScript / TypeScript support** - Formats SQL inside template literals (backtick strings), preserving `${...}` interpolations
- 🔕 **Suppresses false SQL errors** - Hides IDE inspection noise caused by `${...}` interpolations in JS/TS SQL strings

---

## ⌨️ Usage

### 📝 Formatting SQL Queries

1. Install **KongQuery** from the [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/28845-kongquery)
2. Open any Spring Data JPA repository file
3. Place your cursor inside an `@Query` string
4. Press **`Ctrl + Alt + L`** (**macOS:** `⌘ + ⌥ + L`) or right-click → **"Format SQL Query"**
5. Enjoy your neatly formatted query ✨

### 🎯 Generating Projection Interfaces

1. **Select SQL text** in your editor (including DTO constructor or regular SELECT with AS aliases)
2. Press **`Alt + Shift + P`** (**macOS:** `⌥ + ⇧ + P`) or right-click → **"Generate Projection Interface from SQL"**
3. **Enter interface name** (e.g., `UserResponse`, `BookDto`)
4. **Choose package/directory** where to create the interface
5. ✨ **Interface is automatically generated** with proper getter methods!

#### Supported Formats:
- **DTO Constructors**: `SELECT new com.example.Dto(field1, field2)`
- **AS Aliases**: `SELECT table.field1 AS field1, table.field2 AS field2`
- **Mixed**: Both formats in the same query

#### Example:
```java
// Select this SQL:
SELECT new com.example.payload.response.UserResponse(
        u.userId,
        u.username,
        u.email,
        u.active
        )
FROM Users u

// Generated Interface:
public interface UserResponse {
    Object getUserId();
    Object getUsername();
    Object getEmail();
    Object getActive();
}

```

### 🟨 SQL in JavaScript / TypeScript Files

KongQuery also works in `.js`, `.jsx`, `.ts`, `.tsx` files (e.g. `pg` / node-postgres code):

1. Open any JS/TS file containing SQL in a **template literal**
2. Press **`Ctrl + Alt + L`** (**macOS:** `⌘ + ⌥ + L`) or right-click → **"Format SQL Query"**

```sql
UPDATE table_name
SET column_a = v.column_a,
    column_b = v.column_b
FROM (VALUES ${placeholder}) AS v (id, column_a, column_b)
WHERE table_name.id = v.id
RETURNING *
```

`${...}` interpolations are preserved while formatting. You can also select SQL and press **`Alt + Shift + P`** (**macOS:** `⌥ + ⇧ + P`) to generate a projection interface, same as in Java.

### 🔄 Automatic Formatting

KongQuery also automatically formats SQL when you:
- Use IntelliJ's code formatting (**Ctrl + Alt + L** on Windows/Linux, **⌘ + ⌥ + L** on macOS)
- Save files (if enabled in settings)
- Manually trigger formatting via the context menu

---

## ⌨️ Quick Reference

| Action | Windows/Linux | macOS | Description |
|--------|-------------|-------|-------------|
| **Format SQL Query** | `Ctrl + Alt + L` | `⌘ + ⌥ + L` | Format SQL at cursor or all queries in file |
| **Generate Projection Interface** | `Alt + Shift + P` | `⌥ + ⇧ + P` | Generate Java interface from selected SQL |
| **Context Menu** | Right-click | Right-click | Access both formatting and projection generation |

---

## 🧰 Configuration (optional)

KongQuery automatically detects SQL dialects and works out of the box.
Advanced configuration will be available in **Settings → Tools → KongQuery** (coming soon).

---

## 📦 Installation

### From IntelliJ Marketplace
- Go to **Settings → Plugins → Marketplace**
- Search for **“KongQuery”**
- Click **Install**

### Manual install
Download the latest `.zip` from the [Releases](https://github.com/your-username/kongquery/releases) page,
then install it via **Settings → Plugins → Install Plugin from Disk...**

---

## 🦍 Why "KongQuery"?

Because Kong is mighty and powerful — just like how KongQuery tackles your complex SQL queries with strength and precision! 😄
It's powerful, fast, and intelligent — your dominant SQL formatter that rules them all.

---

## 🪶 License

MIT © 2025 [Kongkiat](https://github.com/KONGKIAT888/kong-query/blob/main/LICENSE)

---

> "Format your @Query — beautifully, intelligently, and effortlessly." 🦍💪
