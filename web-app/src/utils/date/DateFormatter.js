// Приведение общего формата даты к определённому формату
Date.prototype.format = function (format = 'yyyy-mm-dd') {
    const replaces = {
        yyyy: this.getFullYear(),
        mm: ('0' + (this.getMonth() + 1)).slice(-2),
        dd: ('0' + this.getDate()).slice(-2),
        hh: ('0' + this.getHours()).slice(-2),
        MM: ('0' + this.getMinutes()).slice(-2),
        ss: ('0' + this.getSeconds()).slice(-2)
    };
    let result = format;
    for (const replace in replaces) {
        result = result.replace(replace, replaces[replace]);
    }
    return result;
};

export const getFormattedDate = (date, formatString = 'yyyy-mm-dd') => {
    return (new Date(date)).format(formatString);
};