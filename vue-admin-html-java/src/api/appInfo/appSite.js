/**
 * Created by lk on 17/6/4.
 */
import axios from "../../utils/axios";

// 谁最懂我相关

// 列表
export function appSiteList(query) {
    return axios({
        url: "/admin/appInfo/list",
        method: "get",
        params: query
    });
}

// 更新安装量
export function setInstallLimit(data) {
    return axios({
        url: "/admin/appInfo/installLimit",
        method: "post",
        params: data
    });
}

// 上架应用
export function enableAppInfo(data) {
    return axios({
        url: "/admin/appInfo/enable",
        method: "post",
        params: data
    });
}

// 下架应用
export function disableAppInfo(data) {
    return axios({
        url: "/admin/appInfo/disable",
        method: "post",
        params: data
    });
}

export function getUrl(query) {
    return axios({
        url: "/admin/appInfo/url",
        method: "get",
        params: query
    });
}

// 所有列表
export function appSiteAllList(query) {
    return axios({
        url: "/admin/appInfo/allList",
        method: "get",
        params: query
    });
}