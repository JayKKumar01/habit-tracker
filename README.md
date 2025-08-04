# Habit Tracker – Full Stack Web Application

## 🚀 Live Demo

Explore the live application here:
🔗 **[http://jaykkumar01-habit-tracker.s3-website.eu-north-1.amazonaws.com/](http://jaykkumar01-habit-tracker.s3-website.eu-north-1.amazonaws.com/)**

## 📁 Frontend Repository

View the source code for the React.js frontend:
📂 **[JayKKumar01/habit-tracker-frontend](https://github.com/JayKKumar01/habit-tracker-frontend)**

---

## 📌 Overview

**Habit Tracker** is a full-stack web application developed using **React.js** for the frontend and **Spring Boot** for the backend. It helps users build positive routines by allowing them to:

* Create custom daily or weekly habits
* Log progress with a visual tracker
* Organize habits using tags
* Maintain consistency through a streak-based view

The application focuses on simplicity, usability, and performance—making habit tracking easy and rewarding.

---

## ✅ Key Features

* 🔐 **User Authentication** — Register and log in securely
* 📝 **Habit Management** — Create, edit, and delete habits (daily or weekly)
* 📊 **Progress Logging** — Track daily completions and view progress history
* 🏷️ **Tagging System** — Assign tags for better habit organization
* 📱 **Responsive UI** — Optimized for both desktop and mobile browsers
---

## 🔄 Entity Relationships (JPA)

The backend follows a clean, normalized relational structure using **JPA annotations** to define relationships between entities. Here’s how the core entities are connected:

---

### 🧑 `User` Entity

Each user can have:

* **Multiple habits** (`@OneToMany`)
* **One profile** (`@OneToOne`)

```java
// User.java

@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Habit> habits = new ArrayList<>();

@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
private Profile profile;
```

---

### 📋 `Habit` Entity

Each habit:

* Belongs to **one user** (`@ManyToOne`)
* Has **many logs** (`@OneToMany`)
* Can have **multiple tags** (`@ManyToMany`)

```java
// Habit.java

@ManyToOne
@JoinColumn(name = "user_id", nullable = false)
private User user;

@OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
private List<HabitLog> habitLogs = new ArrayList<>();

@ManyToMany
@JoinTable(
    name = "habit_tag_mapping",
    joinColumns = @JoinColumn(name = "habit_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
)
private Set<Tag> tags = new HashSet<>();
```

---

### 🧠 `Profile` Entity

Each profile is tied to exactly **one user**.

```java
// Profile.java

@OneToOne
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User user;
```

---

### ✅ `HabitLog` Entity

Each log entry is connected to:

* **One habit** (`@ManyToOne`)

```java
// HabitLog.java

@ManyToOne
@JoinColumn(name = "habit_id", nullable = false)
private Habit habit;
```

It also enforces **one log per habit per date** via a unique constraint:

```java
@Table(
  name = "habit_logs",
  uniqueConstraints = @UniqueConstraint(columnNames = {"habit_id", "date"})
)
```

---

### 🏷️ `Tag` Entity

Each tag:

* Can be assigned to **multiple habits** (`@ManyToMany`, inverse side)

```java
// Tag.java

@ManyToMany(mappedBy = "tags")
private Set<Habit> habits = new HashSet<>();
```

---

### 💡 Summary Table

| Entity   | Relationship | Target Entity | Cardinality  | Annotation Used |
| -------- | ------------ | ------------- | ------------ | --------------- |
| User     | Habits       | Habit         | One-to-Many  | `@OneToMany`    |
| User     | Profile      | Profile       | One-to-One   | `@OneToOne`     |
| Habit    | User         | User          | Many-to-One  | `@ManyToOne`    |
| Habit    | Logs         | HabitLog      | One-to-Many  | `@OneToMany`    |
| Habit    | Tags         | Tag           | Many-to-Many | `@ManyToMany`   |
| Tag      | Habits       | Habit         | Many-to-Many | `@ManyToMany`   |
| HabitLog | Habit        | Habit         | Many-to-One  | `@ManyToOne`    |

---
