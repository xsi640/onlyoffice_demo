import * as MESSAGE from '../consts/message'
import * as FileAPI from '../service/fileapi'

export const list = () => {
    return (dispatch) => {
        FileAPI.getFiles(json => {
            dispatch({
                type: MESSAGE.FILE_LIST,
                payload: json
            });
        }, err => {
            dispatch({
                type: MESSAGE.FILE_LIST,
                error: err
            });
        });
    }
}

export const clear = () => {
    return (dispatch) => {
        dispatch({
            type: MESSAGE.CLEAR
        })
    }
}