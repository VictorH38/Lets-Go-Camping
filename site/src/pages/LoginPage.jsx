import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { setCookie } from "../utils/cookieHelper";
import { useAuth } from '../AuthContext';

function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [errors, setErrors] = useState("");
    const [consecutiveFailures, setConsecutiveFailures] = useState(0);
    const navigate = useNavigate();
    const { setIsLoggedIn } = useAuth();

    const handleLogin = async (e) => {

        // keep track of # of consecutive failures
        let $consecutiveFailures = consecutiveFailures;

        const maxLoginsPerSequence = 4;
        e.preventDefault();
        const response = await fetch("api/users/signin", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password }),
        });

        const data = await response.json();

        if (data && data.token) {
            setCookie("token", data.token);
            setCookie("user_id", data.id);

            setIsLoggedIn(true);
            navigate("/");
        } else {

            if(data.message === "Invalid password" ||  data.message === "[Test] Account no longer locked") {

                // If account is no longer locked, reset consecutive failure counter
                if(errors === "Account is temporarily locked" || data.message === "[Test] Account no longer locked") {
                    $consecutiveFailures = 1;
                } else {

                    // If account is not locked, add to consecutive failure counter
                    $consecutiveFailures++;
                }

                // Display message
                setErrors(`Invalid password. ${maxLoginsPerSequence - $consecutiveFailures} attempts remaining in the next minute.`);
            } else {

                // Otherwise, show other error message from server
                setErrors(data.message);
            }

            // update failure count
            setConsecutiveFailures($consecutiveFailures);
        }
    };

    return (
        <div className="d-flex justify-content-center my-auto vertical-center">
            <div className="d-flex flex-column" style={{ width: "750px" }}>
                <form onSubmit={handleLogin}>
                    <div className="form-group d-flex flex-row justify-content-center my-3">
                        <label htmlFor="email" className="form-check-label my-1 col-sm-3" style={{ textAlign: "right" }}>
                            Email
                        </label>
                        <input type="email" id="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                    </div>

                    <div className="form-group d-flex flex-row justify-content-center my-3">
                        <label htmlFor="password" className="form-check-label my-1 col-sm-3" style={{ textAlign: "right" }}>
                            Password
                        </label>
                        <input type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                    </div>

                    <div className="form-group d-flex flex-row justify-content-center my-3">
                        <input type="submit" value="Login" className="btn btn-primary"/>
                        <input type="button" value="Cancel" className="ml-3 btn btn-secondary bg-danger"
                               onClick={() => {

                                   // clear form
                                   setEmail("");
                                   setPassword("");

                                   // go to homepage
                                   window.location.href = "/";
                               }}
                        />
                    </div>

                    {errors && (
                        <div className="form-group d-flex flex-row justify-content-center my-3">
                            <div id="error" style={{color: "red"}}>{errors}</div>
                        </div>
                    )}
                </form>

                <div className="text-center">
                    <p>
                        Don't have an account yet?
                        <Link to="/signup"> Sign up!</Link>
                    </p>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
