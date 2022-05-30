import { createContext } from "react";

function noop() {}

const AuthContext = createContext({
    usersId: null,
    rolesId: null,
    tokens: null,
    modules: null,
    typeAuth: null,
    isAuthenticated: false,
    login: noop,
    logout: noop
});

export default AuthContext;