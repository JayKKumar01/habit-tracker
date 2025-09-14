import BASE_API_URL from "../config/config";

const TAG_URL = `${BASE_API_URL}/tags`;

// Get token from localStorage helper
const getAuthHeaders = () => {
    const token = localStorage.getItem("token");
    return {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
    };
};

export const addHabitTag = async (userId, tagData) => {
    const res = await fetch(`${TAG_URL}/add-habit-tag/${userId}`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(tagData),
    });

    const data = await res.json();

    if (!res.ok) {
        const errorMsg = data.error || data.message || "Failed to add habit tag.";
        throw new Error(errorMsg);
    }

    return data;
}
export const removeHabitTag = async (userId, tagData) => {
    const res = await fetch(`${TAG_URL}/remove-habit-tag/${userId}`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(tagData),
    });

    const data = await res.json();

    if (!res.ok) {
        const errorMsg = data.error || data.message || "Failed to remove habit tag.";
        throw new Error(errorMsg);
    }

    return data;
}


