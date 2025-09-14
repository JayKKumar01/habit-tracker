import BASE_API_URL from "../config/config";

const HABIT_URL = `${BASE_API_URL}/habits`;

// Get token and email from localStorage helper
const getAuthHeaders = () => {
    const token = localStorage.getItem("token");
    return {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
    };
};

// ✅ Create a new habit
export const createHabit = async (habitData, userId) => {
    const res = await fetch(`${HABIT_URL}/create/${userId}`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(habitData),
    });

    const data = await res.json();

    if (!res.ok) {
        const errorMsg = data.error || data.message || "Failed to create habit.";
        throw new Error(errorMsg);
    }

    return data;
};
// ✅ Edit an existing habit
export const updateHabit = async (updatedHabitData, userId) => {
    const res = await fetch(`${HABIT_URL}/edit/${userId}`, {
        method: "PUT",
        headers: getAuthHeaders(),
        body: JSON.stringify(updatedHabitData),
    });

    const data = await res.json();

    if (!res.ok) {
        const errorMsg = data.error || data.message || "Failed to update habit.";
        throw new Error(errorMsg);
    }

    return data;
};


export const getHabitsWithLogsByUserId = async (userId) => {
    const res = await fetch(`${HABIT_URL}/habits/${userId}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    const data = await res.json();

    if (!res.ok) {
        const errorMsg = data.error || data.message || "Failed to fetch habits with logs.";
        throw new Error(errorMsg);
    }

    return data;
};



// ✅ Hard delete a habit by ID via URL path
export const deleteHabit = async (userId, habitId) => {
    const res = await fetch(`${HABIT_URL}/delete/${userId}/${habitId}`, {
        method: "DELETE",
        headers: getAuthHeaders(), // includes Authorization header etc.
    });

    const data = await res.json();

    if (!res.ok) {
        const errorMsg = data.error || data.message || "Failed to delete habit.";
        throw new Error(errorMsg);
    }

    return data;
};




