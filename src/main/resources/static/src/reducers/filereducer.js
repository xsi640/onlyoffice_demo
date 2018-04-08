import * as MESSAGE from '../consts/message'

const initialStatus = {};

export const FileReducer = (state = initialStatus, action) => {
    switch (action.type) {
        case MESSAGE.FILE_LIST:
            return {
                ...initialStatus,
                files: action.payload,
                error: action.error
            }
        case MESSAGE.CLEAR:
            return {};
        default:
            return state;
    }
}
