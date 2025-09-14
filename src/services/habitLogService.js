import BASE_API_URL from "../config/config";

const HABIT_LOG_URL = `${BASE_API_URL}/habit-log`;

// Get token from localStorage helper
const getAuthHeaders = () => {
    const token = localStorage.getItem("token");
    return {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
    };
};

// ✅ Update or Create Habit Log
export const updateHabitLog = async (userId, habitLogData) => {
    const res = await fetch(`${HABIT_LOG_URL}/update/${userId}`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(habitLogData),
    });

    const data = await res.json();

    if (!res.ok) {
        const errorMsg = data.error || data.message || "Failed to update habit log.";
        throw new Error(errorMsg);
    }

    return data;
};


