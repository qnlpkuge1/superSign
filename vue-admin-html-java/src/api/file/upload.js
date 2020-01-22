/**
 * 上传相关
 */
import axios from "../../utils/axios";

// 获取七牛上传 upToken
export function qiuNiuUpToken(query) {
    return axios({
        url: "/admin/file/upload/qiuNiuUpToken",
        method: "get",
        params: query
    });
}

// 上传文件
export function createFile(url, formdata) {
    return axios({
        url: url,
        method: "post",
        data: formdata
    });
}

export function updateFile(appInfoId, formdata) {
    return axios({
        url: "/admin/file/upload/"+appInfoId+"/updateFile",
        method: "post",
        data: formdata
    });
}


export function uploadIcon(formdata) {
    return axios({
        url: "/admin/file/upload/icon",
        method: "post",
        data: formdata
    });
}


