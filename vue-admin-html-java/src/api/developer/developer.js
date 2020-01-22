/**
 * Created by lk on 17/6/4.
 */
import axios from "../../utils/axios";

// 谁最懂我相关

// 列表
export function developerList(query) {
    return axios({
        url: "/admin/developer/list",
        method: "get",
        params: query
    });
}

export function pingLogList(query) {
    return axios({
        url: "/admin/developer/pingLog",
        method: "get",
        params: query
    });
}

export function addDeveloper(data) {
    return axios({
        url: "/admin/developer/add",
        method: "post",
        data: data
    });
}

export function resetInstall(data) {
    return axios({
        url: "/admin/developer/resetInstall",
        method: "post",
        params: data
    });
}

export function developerListUnallocat() {
    return axios({
        url: "/admin/developer/list/unallocat",
        method: "get"
    });
}
