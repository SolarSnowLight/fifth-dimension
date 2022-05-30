module.exports = class SoundDto {
    id;
    subscribe;
    title;
    type;

    constructor(model) {
        this.id = model.id;
        this.subscribe = model.subscribe;
        this.title = model.title;
        this.type = model.type;
    }
}