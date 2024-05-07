import React, { useState } from "react";
import {Link, useNavigate} from "react-router-dom";
import { setCookie } from "../utils/cookieHelper"
import { useAuth } from '../AuthContext';

function SignupPage(){

    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [passwordConfirmation, setPasswordConfirmation] = useState('');
    const [errors, setErrors] = useState('');
    const navigate = useNavigate();
    const { setIsLoggedIn } = useAuth();

    return(
        <>
            <div className = "d-flex justify-content-center my-auto">
                <div className = "d-flex flex-column" style={{width:"750px"}}>
                    <form
                        onSubmit={async (e) => {
                            e.preventDefault();
                            if (passwordConfirmation !== password)
                            {
                                setErrors("Passwords don't match");
                            }
                            else
                            {
                                fetch("api/users/signup", {
                                    method: "POST",
                                    headers: {
                                     "Content-Type": "application/json",
                                    },
                                    body: JSON.stringify({
                                        email: email,
                                        password: password,
                                        name: name
                                    }),
                                    })
                                    .then((response) => response.json())
                                    .then((data) => {
                                     if (data && data.token) {
                                         setCookie("token", data.token);
                                         setCookie("user_id", data.id);

                                         setIsLoggedIn(true);
                                         navigate("/");
                                     } else {
                                         setErrors(data.message)
                                     }
                                });
                            }
                        }}

                    >
                        <div className = "form-group d-flex flex-row justify-content-center my-3">
                            <label className="form-check-label my-1 col-sm-3" htmlFor="name" style={{display:"block", textAlign:"right"}}>Name</label>
                            <input
                                type="text"
                                id="name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                required
                            />
                        </div>

                        <div className = "form-group d-flex flex-row justify-content-center my-3">
                            <label className="form-check-label my-1 col-sm-3" htmlFor="email" style={{display:"block", textAlign:"right"}}>Email</label>
                            <input
                                type="email"
                                id="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div className = "form-group d-flex flex-row justify-content-center my-3">
                            <label className="form-check-label my-1 col-sm-3" htmlFor="password" style={{display:"block", textAlign:"right"}}>Password</label>
                            <input
                                type="password"
                                id="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <div className = "form-group d-flex flex-row justify-content-center my-3">
                            <label className="form-check-label my-1 col-sm-3" htmlFor="passwordConfirmation" style={{display:"block", textAlign:"right"}}>Confirm Password</label>
                            <input
                                type="password"
                                id="passwordConfirmation"
                                value={passwordConfirmation}
                                onChange={(e) => setPasswordConfirmation(e.target.value)}
                                required
                            />
                        </div>

                        <div className="form-group d-flex flex-row justify-content-center my-3">
                            <input type="submit" value="Sign Up" className="btn btn-primary"/>
                            <input type="button" value="Cancel" className="ml-3 btn btn-secondary bg-danger"
                                   onClick={() => {

                                       // clear form
                                       setName("");
                                       setEmail("");
                                       setPassword("");
                                       setPasswordConfirmation("");

                                       // go to homepage
                                       window.location.href = "/";
                                   }}
                            />
                        </div>
                        {errors &&
                            <div className = "form-group d-flex flex-row justify-content-center my-3">
                                <div id="error" style={{color: "red"}}>{errors}</div>
                            </div>
                            }
                    </form>

                    <div className="text-center">
                        <p>
                            Already have an account?
                            <Link to="/login"> Log in!</Link>
                        </p>
                    </div>
                </div>
            </div>
        </>
    );
}

export default SignupPage;