module.exports = class TarrifInfoDto {
    uuid;
    value;
    description;
    price;
    period;


    constructor(model) {
        this.uuid = model.uuid;
        this.value = model.value;
        this.description = model.description;
        this.price = model.price;
        this.period = model.period;
    }
}