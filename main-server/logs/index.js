const { createLogger, format, transports } = require('winston');
const { combine, timestamp, json } = format;

var logger = createLogger({
    format: combine(
        timestamp({
            format: 'YYYY-MM-DD HH:mm:ss'
        }),
        json(),
    ),
    transports: [
        new (transports.File)({
            name: 'info-file',
            filename: `${process.env.LOG_FOLDER_PATH}/info.log`,
            level: 'info'
        }),
        new (transports.File)({
            name: 'error-file',
            filename: `${process.env.LOG_FOLDER_PATH}/error.log`,
            level: 'error'
        }),
        new (transports.File)({
            name: 'debug-file',
            filename: `${process.env.LOG_FOLDER_PATH}/debug.log`,
            level: 'debug'
        }),
        new (transports.File)({
            name: 'warn-file',
            filename: `${process.env.LOG_FOLDER_PATH}/warn.log`,
            level: 'warn'
        })
    ]
});

module.exports = logger;