/**
 * Created by lk on 17/6/4.
 */
import axios from "../../utils/axios";

// 谁最懂我相关

// 列表
export function installList(query) {
    return axios({
        url: "/admin/installLog/list",
        method: "get",
        params: query
    });
}

