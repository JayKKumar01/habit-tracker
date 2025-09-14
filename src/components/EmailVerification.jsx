import React, { useState } from "react";
import { sendVerificationCode, verifyEmailCode } from "../services/userAuthService";

const EmailVerification = ({ onVerified }) => {
    const [email, setEmail] = useState("");
    const [code, setCode] = useState("");
    const [codeSent, setCodeSent] = useState(false);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null);

    const handleSendCode = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage(null);

        try {
            await sendVerificationCode(email);
            setCodeSent(true);
            setMessage({ type: "success", text: "✅ Verification code sent to your email." });
        } catch (err) {
            setMessage({ type: "error", text: `❌ ${err.message}` });
        } finally {
            setLoading(false);
        }
    };

    const handleVerifyCode = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage(null);

        try {
            await verifyEmailCode(email, code);
            setMessage({ type: "success", text: "✅ Email verified successfully!" });
            onVerified(email); // proceed to signup
        } catch (err) {
            setMessage({ type: "error", text: `❌ ${err.message}` });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container">
            <h1>Email Verification</h1>

            {!codeSent ? (
                <form onSubmit={handleSendCode}>
                    <div className="input-group">
                        <label>Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            disabled={loading}
                        />
                    </div>

                    <button type="submit" disabled={loading}>
                        {loading ? "Sending code..." : "Send Verification Code"}
                    </button>
                </form>
            ) : (
                <form onSubmit={handleVerifyCode}>
                    <div className="input-group">
                        <label>Enter Verification Code</label>
                        <input
                            type="text"
                            value={code}
                            onChange={(e) => setCode(e.target.value)}
                            required
                            disabled={loading}
                        />
                    </div>

                    <button type="submit" disabled={loading}>
                        {loading ? "Verifying..." : "Verify Code"}
                    </button>
                </form>
            )}

            {message && (
                <p className={`message ${message.type}`}>
                    {message.text}
                </p>
            )}
        </div>
    );
};

export default EmailVerification;
