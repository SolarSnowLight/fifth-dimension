module.exports = class UserDto {
    users_id;
    date_open;
    type;
    subscribe;
    description;
    title;

    constructor(model) {
        this.users_id = model.users_id;
        this.date_open = model.date_open;
        this.subscribe = model.subscribe;
        this.description = model.description;
        this.title = model.title;
        this.type = model.type;
    }
}