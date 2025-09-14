import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { verifyToken } from "../services/userAuthService";

const ProtectedRoute = ({ children }) => {
    const [authorizedUser, setAuthorizedUser] = useState(null);

    useEffect(() => {
        // Immediately invoke async function inside useEffect
        (async () => {
            const user = await verifyToken();
            setAuthorizedUser(user || false);
        })();
    }, []);

    if (authorizedUser === null) return <p>Loading...</p>;
    if (authorizedUser === false) return <Navigate to="/" />;

    return children(authorizedUser); // children must be a function that receives the user
};

export default ProtectedRoute;
