module.exports = class UserInfoDto {
    name;
    surname;

    constructor(model) {
        this.name = model.name;
        this.surname = model.surname;
    }
}