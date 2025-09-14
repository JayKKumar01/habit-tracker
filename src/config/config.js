// src/config/config.js
const BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:9090/api";

export default BASE_URL;

// Set the base path for GitHub Pages
export const BASENAME =
    process.env.NODE_ENV === "production" ? "/habit-tracker-frontend" : "/";
