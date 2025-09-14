import BASE_API_URL from "../config/config";

const PROFILE_URL = `${BASE_API_URL}/profile`;

// Get token and email from localStorage helper
const getAuthHeaders = () => {
    const token = localStorage.getItem("token");
    return {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
    };
};

// ✅ Get profile by user email
export const getProfile = async (userId) => {
    const res = await fetch(`${PROFILE_URL}/user/${userId}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    // Check for empty body
    const isJson = res.headers
        .get("content-type")
        ?.includes("application/json");

    const data = isJson ? await res.json() : null;

    if (!res.ok) {
        const errorMsg = data?.error || data?.message || "Failed to fetch profile.";
        throw new Error(errorMsg);
    }

    return data;
};

// ✅ Save or update profile bio
export const saveOrUpdateProfile = async (userId, profileData) => {
    const res = await fetch(`${PROFILE_URL}/save/${userId}`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(profileData), // { name, bio }
    });

    const data = await res.json();

    if (!res.ok) {
        const errorMsg = data.error || data.message || "Failed to save profile.";
        throw new Error(errorMsg);
    }

    return data;
};

