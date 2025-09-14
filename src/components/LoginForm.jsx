import React, { useState } from "react";
import { loginUser } from "../services/userAuthService";
import { useNavigate } from "react-router-dom";

const LoginForm = () => {
    const [formData, setFormData] = useState({
        email: "",
        password: "",
    });

    const [message, setMessage] = useState(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData((prev) => ({
            ...prev,
            [e.target.name]: e.target.value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage(null);
        setLoading(true);

        try {
            const data = await loginUser(formData);

            localStorage.setItem("token", data.token);
            localStorage.setItem("email", formData.email);

            setMessage({ type: "success", text: "✅ Login successful!" });
            setFormData({ email: "", password: "" });

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
                <label>Email</label>
                <input
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                    disabled={loading}
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
                {loading ? "Logging in..." : "Login"}
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

export default LoginForm;
