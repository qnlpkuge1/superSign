/**
 * Created by lk on 17/6/4.
 */
import axios from "../../utils/axios";

// 谁最懂我相关

export function allocatDeveloper(data) {
    return axios({
        url: "/admin/developer/allocat",
        method: "post",
        data: data
    });
}
