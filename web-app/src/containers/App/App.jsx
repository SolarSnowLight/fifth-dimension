import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import useRoutes from '../../routes/routes';
import useAuth from '../../hooks/auth.hook';
import AuthContext from '../../context/AuthContext.js';
import Navbar from '../Navbar';
import Header from '../Header';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import styles from './App.module.css';

const App = () => {
    const { usersId, rolesId, tokens, modules, typeAuth, login, logout } = useAuth();
    const isAuthenticated = (tokens)? !!tokens.access_token : null;
    const routes = useRoutes(isAuthenticated, modules);

    return (
        <AuthContext.Provider value={{
            usersId, rolesId, tokens, modules, typeAuth, login, logout, isAuthenticated
        }}>
            <BrowserRouter>
            <Header/>
                { /* Определение основного содержимого веб-приложения */ }
                {isAuthenticated && <Navbar/> }
                <div className={styles["container"]}>
                    {routes}
                </div>

                { /* Определение контейнера для уведомлений */ }
                <ToastContainer
                    position="bottom-left"
                    autoClose={3000}
                    hideProgressBar={false}
                    newestOnTop={false}
                    closeOnClick
                    rtl={false}
                    pauseOnFocusLoss
                    draggable
                    pauseOnHover
                />
            </BrowserRouter>
        </AuthContext.Provider>
    );
}

export default App;