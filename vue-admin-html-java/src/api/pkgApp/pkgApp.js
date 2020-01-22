/**
 * Created by lk on 17/6/4.
 */
import axios from "../../utils/axios";

// 谁最懂我相关

// 列表
export function pkgAppList(query) {
    return axios({
        url: "/admin/pkgApp/list",
        method: "get",
        params: query
    });
}

export function addPkgApp(data) {
    return axios({
        url: "/admin/pkgApp/add",
        method: "post",
        data: data
    });
}

export function getUrl(query) {
    return axios({
        url: "/admin/pkgApp/url",
        method: "get",
        params: query
    });
}
