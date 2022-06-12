
const AuthApiConstants = {
    main: '/api/auth',

    register: '/register',
    login: '/login',
    logout: '/logout',
    refresh: '/refresh/token',
    oauth: '/oauth',
    activate: '/activate/:link',
    verification: '/verification'  
}

module.exports = AuthApiConstants;