import { useState, useCallback, useContext } from "react";
import AuthContext from "../context/AuthContext";
import AuthApiConstants from "../constants/addresses/api/auth.api.constants";
import MainApiConstants from "../constants/addresses/api/main.api.constants";
import StorageNameConstants from "../constants/values/storage.name.constants";
import { isEmptyObject } from "../utils/data/DataUtils";

// Хук работы с сетевыми запросами
const useHttp = () => {
    // Использование контекста
    const auth = useContext(AuthContext);

    // Состояния загрузки и ошибки

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const originalRequest = useCallback(async (url, method = 'GET', body = null, headers = {}) => {
        setLoading(true);

        try {
            const response = await fetch(
                (MainApiConstants.main_server + url), 
                { 
                    method, 
                    body, 
                    headers 
                }
            );
            const data = await response.json();

            setLoading(false);

            return {
                response,
                data
            };
        } catch(e){
            setLoading(false);
            setError(e.message);
            console.log(e);
        }
    }, []);

    const refreshToken = useCallback(async (token, typeAuth) => {
        setLoading(true);

        try {
            const response = await fetch(
                (MainApiConstants.main_server + AuthApiConstants.refresh),
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        refresh_token: token,
                        type_auth: typeAuth
                    })
                }
            );

            const data = await response.json();

            auth.login(data.users_id, data.roles_id, data.tokens, data.modules, data.type_auth);

            setLoading(false);

            return data;
        } catch(e){
            setLoading(false);
            setError(e.message);
            throw e;
        }
    }, []);

    const request = useCallback(async (url, method = 'GET', body = null, headers = {}, multipart = false) => {
        setLoading(true);
        try {
            if((body) && (!headers['Content-Type']) && (!multipart)){
                body = JSON.stringify(body);
                headers['Content-Type'] = 'application/json';
            }

            let authData = JSON.parse(localStorage.getItem(StorageNameConstants.auth));

            if((authData) && (!isEmptyObject(authData))){
                headers['Authorization'] = `Bearer ${authData.type_auth} ${authData.tokens.access_token}`;
            }

            let { response, data } = await originalRequest(url, method, body, headers);

            // Status Code 401 - Unauthorized
            if(response.status === 401){
                authData = await refreshToken(authData.tokens.refresh_token, authData.type_auth);
                headers['Authorization'] = `Bearer ${authData.type_auth} ${authData.tokens.access_token}`;

                const updateResponse = await originalRequest(url, method, body, headers);

                response = updateResponse.response;
                data = updateResponse.data;
            }

            setLoading(false);

            return data;
        } catch(e){
            setLoading(false);
            setError(e.message);
            throw e;
        }
    }, []);

    const clearError = useCallback(() => setError(null), []);

    return { loading, request, error, clearError };
};

export default useHttp;