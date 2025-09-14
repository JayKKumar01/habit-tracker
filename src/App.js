import React, { useState, useEffect } from "react";
import { Routes, Route, useLocation } from "react-router-dom";
import SignupForm from "./components/SignupForm";
import LoginForm from "./components/LoginForm";
import Dashboard from "./components/Dashboard";
import AuthLink from "./components/AuthLink";
import ProtectedRoute from "./components/ProtectedRoute";
import EmailVerification from "./components/EmailVerification";
import "./App.css";

function App() {
    const [verifiedEmail, setVerifiedEmail] = useState(null);
    const location = useLocation();

    // ✅ Reset verifiedEmail whenever user navigates to "/"
    useEffect(() => {
        if (location.pathname === "/") {
            setVerifiedEmail(null);
        }
    }, [location.pathname]);

    return (
        <Routes>
            <Route
                path="/"
                element={
                    <div className="container">
                        <h1>Habit Tracker - Login</h1>
                        <LoginForm />
                        <AuthLink type="signup" />
                    </div>
                }
            />
            <Route
                path="/signup"
                element={
                    <div className="container">
                        {!verifiedEmail ? (
                            <>
                                <h1>Verify Your Email</h1>
                                <EmailVerification onVerified={setVerifiedEmail} />
                                <AuthLink type="login" />
                            </>
                        ) : (
                            <>
                                <h1>Habit Tracker - Signup</h1>
                                <SignupForm prefilledEmail={verifiedEmail} />
                                <AuthLink type="login" />
                            </>
                        )}
                    </div>
                }
            />
            <Route
                path="/dashboard"
                element={
                    <ProtectedRoute>
                        {(user) => <Dashboard user={user} />}
                    </ProtectedRoute>
                }
            />
        </Routes>
    );
}

export default App;
