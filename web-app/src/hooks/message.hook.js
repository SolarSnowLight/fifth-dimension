import { useCallback } from "react";
import { toast } from "react-toastify";

// Конфигурация toast (настройка)
toast.configure();

// Реализация хука сообщений
const useMessage = () => {
    return useCallback((text, type) => {
        if(!text){
            return;
        }

        switch(type){
            case "info": {
                toast.info(text);
                break;
            }
            case "success": {
                toast.success(text);
                break;
            }
            case "warning": {
                toast.warning(text);
                break;
            }
            case "error": {
                toast.error(text);
                break;
            }
            case "dark": {
                toast.dark(text);
                break;
            }
            default: {
                toast(text);
                break;
            }
        }

    }, []);

};

export default useMessage;
