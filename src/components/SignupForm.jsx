import React, { useState, useEffect } from "react";
import { signupUser, loginUser } from "../services/userAuthService";
import { useNavigate } from "react-router-dom";

const SignupForm = ({ prefilledEmail }) => {
    if (!prefilledEmail) {
        // Immediately throw error — no signup should happen without email
        throw new Error("❌ Email is required to load the signup form.");
    }

    const [formData, setFormData] = useState({
        name: "",
        email: prefilledEmail,
        password: "",
    });

    const [message, setMessage] = useState(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage(null);
        setLoading(true);

        try {
            // 1️⃣ Signup user
            const signupData = await signupUser(formData);
            console.log("✅ Created User:", signupData);

            // 2️⃣ Auto login
            const loginData = await loginUser({
                email: formData.email,
                password: formData.password,
            });

            localStorage.setItem("token", loginData.token);
            localStorage.setItem("email", formData.email);

            setMessage({ type: "success", text: "✅ Signup and login successful!" });

            // 3️⃣ Reset form & redirect
            setFormData({ name: "", email: prefilledEmail, password: "" });
            navigate("/dashboard");
        } catch (err) {
            setMessage({ type: "error", text: `❌ ${err.message}` });
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="form">
            <div className="input-group">
                <label>Name</label>
                <input
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    disabled={loading}
                />
            </div>

            <div className="input-group">
                <label>Email</label>
                <input
                    name="email"
                    type="email"
                    value={formData.email}
                    readOnly
                    disabled
                    style={{ backgroundColor: "#f5f5f5", cursor: "not-allowed" }}
                />
            </div>

            <div className="input-group">
                <label>Password</label>
                <input
                    name="password"
                    type="password"
                    value={formData.password}
                    onChange={handleChange}
                    required
                    disabled={loading}
                />
            </div>

            <button type="submit" className="btn" disabled={loading}>
                {loading ? "Signing up..." : "Signup"}
            </button>

            {message && (
                <p
                    style={{
                        color: message.type === "error" ? "red" : "green",
                        marginTop: "10px",
                    }}
                >
                    {message.text}
                </p>
            )}
        </form>
    );
};

export default SignupForm;
