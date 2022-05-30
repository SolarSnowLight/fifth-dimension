import { useState, useCallback, useEffect } from 'react';
import useHttp from './http.hook';
import useMessage from './message.hook'; 
import StorageNameConstants from '../constants/values/storage.name.constants';
import AuthApiConstants from '../constants/addresses/api/auth.api.constants'; 

const useAuth = () => {
    const [usersId, setUsersId] = useState(null);
    const [rolesId, setRolesId] = useState(null);
    const [tokens, setTokens] = useState(null);
    const [modules, setModules] = useState(null);
    const [typeAuth, setTypeAuth] = useState(null);

    const { loading, request, error, clearError } = useHttp();
    const message = useMessage();

    // Авторизация пользователя
    const login = useCallback((usersId, rolesId, tokens, modules, typeAuth) => {
        // Установка состояний
        setUsersId(usersId);
        setRolesId(rolesId);
        setTokens(tokens);
        setModules(modules);
        setTypeAuth(typeAuth);

        // Сохранение в локальное хранилище данных
        localStorage.setItem(
            StorageNameConstants.auth,
            JSON.stringify({
                users_id: usersId,
                roles_id: rolesId,
                tokens: tokens,
                modules: modules,
                type_auth: typeAuth
            })
        );

    }, []);

    const logout = useCallback(async () => {
        try{
            // Получение данных из хранилища и преобразование JSON-строки в объект
            const data = JSON.parse(localStorage.getItem(StorageNameConstants.auth));

            const response = await request(
                AuthApiConstants.logout,
                'POST',
                {
                    users_id: data.users_id,
                    refresh_token: data.tokens.refresh_token,
                    access_token: data.tokens.access_token,
                    type_auth: data.type_auth
                }
            );

            if(response.message){
                message(response.message, "error");

                const errors = response.errors;
                if(errors){
                    errors.forEach((item) => {
                        message(item.msg, "error");
                    });
                }
            }

            message("Выход из системы", "dark");
        } catch(e){}

        setUsersId(null);
        setRolesId(null);
        setTokens(null);
        setModules(null);
        setTypeAuth(null);

        localStorage.removeItem(StorageNameConstants.auth);
    }, []);

    useEffect(() => {
        const data = JSON.parse(localStorage.getItem(StorageNameConstants.auth));

        if(data){
            login(
                data.users_id,
                data.roles_id,
                data.tokens,
                data.modules,
                data.type_auth
            );
        }

    }, [login]);

    return { usersId, rolesId, tokens, modules, typeAuth, login, logout };
};

export default useAuth;