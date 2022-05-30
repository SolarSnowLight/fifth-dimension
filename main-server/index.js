require('dotenv').config({ path: `.${process.env.NODE_ENV}.env` });

// Importing third-party packages
const express = require('express');
const logger = require('./logs/index');
const cors = require('cors');
const cookieParser = require('cookie-parser');
const errorMiddleware = require('./middlewares/error/error-middleware');
const userMiddleware = require('./middlewares/user/user-middleware');
const schedule = require('node-schedule');

// Routes
const authRouter = require('./routers/auth-routes');
const authManagementRouter = require('./routers/auth-management-routes');
const adminRouter = require('./routers/admin-routes');
const categoryRouter = require('./routers/category-routes');
const courseRouter = require('./routers/course-routes');
const userRouter = require('./routers/user-routes');
const paymentRouter = require('./routers/payment-routes');

// Services
const courseService = require("./services/course/course-service");

// API Constants
const AuthApiConstants = require('./constants/addresses/server/auth-api-constants');
const AuthManagementApiConstants = require('./constants/addresses/server/auth-management-api-constants');
const AdminApiConstants = require('./constants/addresses/server/admin-api-constants');
const CategoryApiConstants = require('./constants/addresses/server/category-api-constants');
const CourseApiConstants = require('./constants/addresses/server/course-api-constants');
const UserApiConstants = require('./constants/addresses/server/user-api-constants');
const PaymentApiConstants = require('./constants/addresses/server/payment-api-constants');

// Development without HTTPS
process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = 0;

const PORT = (process.env.PORT || 5000);
const app = express();

/* Middleware before */
app.use(express.static(__dirname));
app.use(express.json({ extended: true }));
app.use(cookieParser());
app.use(cors({
    credentials: true,
    origin: process.env.CLIENT_URL,
}));
app.use(userMiddleware);

/* Routers */
app.use(AuthApiConstants.main, authRouter);
app.use(AuthManagementApiConstants.main, authManagementRouter);
app.use(AdminApiConstants.main, adminRouter);
app.use(CategoryApiConstants.main, categoryRouter);
app.use(CourseApiConstants.main, courseRouter);
app.use(UserApiConstants.main, userRouter);
app.use(PaymentApiConstants.main, paymentRouter);

/* Middleware after */
app.use(errorMiddleware);

// Точка входа в серверное приложение
const start = async() => {
    try{
        app.listen(PORT, () => {
            console.log(`Server started on PORT = ${PORT}`);
            logger.info({
                port: PORT,
                message: "Запуск сервера"
            });
        });
    }catch(e){
        console.log(e);
    }
}

start();

// Performing the operation every 1 hour
const job = schedule.scheduleJob('*/3600 * * * * *', courseService.CourseUpdateStatus);

process.on('SIGINT', function () { 
    schedule.gracefulShutdown()
    .then(() => process.exit(0))
});
