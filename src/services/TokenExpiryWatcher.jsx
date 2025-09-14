import { useEffect } from "react";
import { jwtDecode } from "jwt-decode"; // ✅ Named import

const TokenExpiryWatcher = ({ token, onExpire }) => {
    useEffect(() => {
        if (!token) return;

        let timeoutId;

        try {
            const { exp } = jwtDecode(token); // ✅ use named function
            const expiryTime = exp * 1000;     // exp is in seconds
            const now = Date.now();            // in milliseconds
            const delay = expiryTime - now;

            if (delay <= 0) {
                onExpire?.(); // Already expired
            } else {
                timeoutId = setTimeout(() => {
                    console.log("⚠️ Token expired, calling onExpire...");
                    onExpire?.();
                }, delay);
            }
        } catch (err) {
            console.error("❌ Invalid token format:", err);
            onExpire?.(); // Treat as expired
        }

        return () => clearTimeout(timeoutId);
    }, [token, onExpire]);

    return null;
};

export default TokenExpiryWatcher;
