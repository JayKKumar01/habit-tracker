# 📊 Habit Tracker – Frontend (React.js)

## 🚀 Live Demo

Explore the live application here:
🔗 **[https://jaykkumar01.github.io/habit-tracker](https://jaykkumar01.github.io/habit-tracker)**

## 🖥️ Backend Repository

For backend APIs and database logic, refer to the Spring Boot repository:
🔗 **[JayKKumar01/habit-tracker-backend](https://github.com/JayKKumar01/habit-tracker-backend)**

---

## 📚 Overview

**Habit Tracker** is a full-stack productivity web application built using **React.js** for the frontend and **Spring Boot** for the backend.
It helps users **build, track, and maintain habits** through a visually engaging and structured interface.

The app encourages consistency and self-improvement by allowing users to:

* Create and manage habits
* Track completion over time
* View weekly summaries and progress
* Assign tags to organize tasks better

---

## ✨ UI Features

The frontend offers a clean and modern interface, split into the following components:

### 🔐 Authentication

* **Login Page (`/`)**: Login using registered email/password.
* **Signup Page (`/signup`)**: Register new account.
* Dynamic **AuthLink** toggles between login and signup routes.
* **ProtectedRoute** ensures only logged-in users can access the dashboard.

---

### 🏠 Dashboard (`/dashboard`)

Main workspace after login, consisting of:

#### 👋 Welcome Header

* Greets user by name
* Displays current week's range
* Includes **Logout** and **Add Habit** buttons

#### 👤 User Profile (`UserInfoCard`)

* Shows name, email, join date, and editable bio
* Editable with save validation and graceful fallbacks

#### ✅ Today’s Tasks (`TodayTaskList`)

* Displays only habits due **today**
* Users can mark habits complete/incomplete with confirmation
* If empty, prompts to create new habits

#### 📋 Habit Overview (`HabitOverviewGrid`)

* Scrollable list of active habits
* Each **HabitCard** includes:

  * Title, description, tags
  * Weekly schedule grid with:

    * ✔ Completed (`green`)
    * ✖ Missed (`red`)
    * ➖ Upcoming (`grey`)
    * 🚫 Not scheduled (`grey-na`)
  * Habit streak counter
  * Edit and delete options

#### 📈 Weekly Progress (`WeeklyProgressBar`)

* Visual progress bars for each habit
* Calculated from current week logs

#### 📅 Weekly Logs (`WeeklyLogList`)

* Collapsible cards showing **each week** since registration
* Per-habit daily logs rendered with emojis and status color
* Current day highlighted for better orientation

---

## ⚙️ Installation & Running Locally

```bash
git clone https://github.com/JayKKumar01/habit-tracker-frontend.git
cd habit-tracker-frontend
npm install
npm start
```

> Make sure the backend is running and accessible via the expected `BASE_URL`.
> Update it in your React app’s config if needed.

---

## 📂 Project Structure (Frontend)

```bash
/src
  ├── components/
  │   ├── authentication/
  │   ├── dashboard/
  │   ├── habit/
  │   ├── modals/
  │   ├── profile/
  │   ├── report/
  │   └── header/
  ├── services/
  ├── utils/
  ├── styles/
  └── App.js
=======
🔗 **[https://jaykkumar01.github.io/habit-tracker](https://jaykkumar01.github.io/habit-tracker)**



---
