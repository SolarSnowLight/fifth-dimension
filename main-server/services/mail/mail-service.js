/*
* Сервис для отправки email-сообщений клиенту
*/

const nodemailer = require('nodemailer');

class MailService{
    constructor(){
        this.transporter = nodemailer.createTransport({
            host: process.env.SMTP_HOST,
            port: process.env.SMTP_PORT,
            secure: false,
            auth: {
                user: process.env.SMTP_USER,
                pass: process.env.SMTP_PASSWORD
            }
        });
    }

    async sendActivationMail(to, link){
        await this.transporter.sendMail({
            from: process.env.SMTP_USER,
            to,
            subject: "Активация аккаунта в приложении \"5 измерение\"",// 'Активация аккаунта на ' + process.env.CLIENT_URL,
            text: '',
            html:
           /* `
                <div>
                    <h1> Для активации аккаунта перейдите по ссылке </h1>
                    <a href="${link}">${link}</a>
                </div>
            `+*/
            `
            <html>
                <head>
                    <meta charset="utf-8" />
                    <title></title>
                </head>
                <style>
                    body {background-color: #FEFEF9;}
                    h2   {color: #181511;}
                    button {
                        color: rgb(0, 0, 0);
                        outline: none;
                        border: none;
                        border-radius: 30px;
                        background-color: #B19472;
                        padding: 8px 16px;
                        margin-top: 16px;
                        cursor: pointer;
                    }
                </style>
                <body>
                    <h2>Подтверждение E-mail</h2>
                    <br><text>Вы получили это письмо, так как Ваш почтовый адрес был указан в приложении "5 измерение".</text> 
                    </br><text>Чтобы подтвердить Вашу почту нажмите ниже: </text></br>
                    <a href="${link}">
                    <button>Подтвердить E-mail</button>
                    </a>
                    <br><br><br>
                    <text>Если Вы не проходили процедуру регистрации в приложении "5 измерение", то не отвечайте на данное сообщение.</text>
                </body>
            </html>
            `
        });
    }
}

module.exports = new MailService();