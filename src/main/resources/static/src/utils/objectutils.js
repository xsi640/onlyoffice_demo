export const getProp = (obj, prop) => {
    if (typeof obj === 'undefined' || obj == null || typeof prop !== 'string' || prop === '')
        return '';

    if (typeof obj[prop] === 'undefined') {
        return '';
    } else {
        return obj[prop];
    }
}

export const fixObject = (obj) => {
    if (typeof obj === 'undefined') {
        return null;
    }

    Object.keys(obj).forEach(item => {
        if (typeof obj[item] === 'undefined') {
            obj[item] = '';
        }
    })
}