/**
 * 公用处理访问webapi的辅助方法
 */
import * as ObjectUtils from '../utils/objectutils'

const headers = { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8', };

/**
 * 请求服务器，获取响应
 * @param {string} url 请求的url
 * @param {string} method 请求的方法，webapi.Method
 * @param {string} parameters 请求的参数
 * @param {func} resolve 成功的返回
 * @param {func} reject 错误的返回
 */
export const Request = (url, method, parameters = {}, resolve, reject) => {
    ObjectUtils.fixObject(parameters);
    if (method === Method.GET) {
        url += '?';
        for (let key in parameters) {
            url += encodeURI(key) + '=' + encodeURI(parameters[key]) + '&';
        }
        url += 'd=' + new Date().getTime();
        fetch(url, { method: 'GET', headers: headers })
            .then(resp => resp.json())
            .then(json => {
                console.log(json);
                if (json.code === 0) {
                    resolve(json.body);
                } else {
                    reject(json.message);
                }
            })
            .catch(err => {
                console.log(err);
                reject(err)
            });
    } else {
        url += '?d=' + new Date().getTime();
        let data = '';
        for (let key in parameters) {
            data += encodeURI(key) + '=' + encodeURI(parameters[key]) + '&'
        }
        fetch(url, { method: method, headers: headers, body: data })
            .then(resp => resp.json())
            .then(json => {
                console.log(json);
                if (json.code === 0) {
                    resolve(json.body);
                } else {
                    reject(json.message);
                }
            })
            .catch(err => {
                console.log(err);
                reject(err)
            });
    }
}

export const Method = {
    GET: 'GET',
    POST: 'POST',
    PUT: 'PUT',
    DELETE: 'DELETE'
}