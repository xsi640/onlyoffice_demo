import { Request, Method } from './webapi';
import * as API from '../consts/api';

export const getFiles = (resolve, reject) => {
    Request(API.getFiles, Method.GET, {}, resolve, reject);
}