module.exports = class ApiError extends Error {
    status;
    message;
    errors;

    constructor(status, message, errors=[]){
        super(message);
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    static UnathorizedError(){
        return new ApiError(401, "Пользователь не авторизован");
    }

    static BadRequest(message, errors=[]){
        return new ApiError(400, message, errors);
    }

    static Forbidden(){
        return new ApiError(403, "Пользователь не имеет доступа для осуществления функции");
    }
}