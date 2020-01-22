<template>

    <div>
        <el-form :inline="true" :model="query" class="query-form" size="mini">
            <el-form-item class="query-form-item">
                <el-input v-model="query.appName" placeholder="应用id"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button-group>
                    <el-button type="primary" icon="el-icon-refresh" @click="onReset();"></el-button>
                    <el-button type="primary" icon="search" @click="onSubmit">查询</el-button>
                </el-button-group>
            </el-form-item>
        </el-form>
        <el-table
            v-loading="loading"
            :data="list"
            style="width: 100%;">
            <el-table-column
                label="ID"
                prop="id"
                width="80"
                fixed>
            </el-table-column>
            <el-table-column
                label="帐号"
                prop="developerName"
                fixed>
            </el-table-column>
            <el-table-column
                label="检测结果"
                prop="status"
                :formatter="formatStatus"
                width="120"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="检测时间"
                prop="updateTime">
                <template slot-scope="scope">
                    <i class="el-icon-time"></i>
                    <span>{{ scope.row.createTime | formatDateStr('yyyy-MM-dd hh:mm:ss') }}</span>
                </template>
            </el-table-column>
            <el-table-column
                label="操作"
                fixed="right">
                <template slot-scope="scope">
                </template>
            </el-table-column>
        </el-table>

        <el-pagination
            :page-size="query.limit"
            @current-change="handleCurrentChange"
            layout="prev, pager, next"
            :total="total">
        </el-pagination>
    </div>

</template>

<script>
import { pingLogList } from "../../api/developer/developer";
const formJson = {
    installLimit: 100,
    id: null
};
export default {
    data() {
        return {
            query: {
                page: 1,
                limit: 20
            },
            list: [],
            appInfo: {},
            total: 0,
            loading: true,
            index: null,
            formName: null,
            formLoading: false,
            formVisible: false,
            formData: formJson,
            ipaFilePath: "",
            ipaFilePathUrl: "",
            formRules: {},
            deleteLoading: false
        };
    },
    components: {},
    methods: {
        onReset() {
            this.$router.push({
                path: ""
            });
            this.query = {
                appName: "",
                page: 1,
                limit: 20
            };
            this.getList();
        },
        onSubmit() {
            this.$router.push({
                path: "",
                query: this.query
            });
            this.getList();
        },
        handleSizeChange(val) {
            this.query.limit = val;
            this.getList();
        },
        handleCurrentChange(val) {
            this.query.page = val;
            this.getList();
        },
        getList() {
            this.loading = true;
            pingLogList(this.query)
                .then(response => {
                    this.loading = false;
                    this.list = response.data.list || [];
                    this.total = response.data.total || 0;
                })
                .catch(() => {
                    this.loading = false;
                    this.list = [];
                    this.total = 0;
                });
        },
        // 刷新表单
        resetForm() {
            if (this.$refs["dataForm"]) {
                // 清空验证信息表单
                this.$refs["dataForm"].clearValidate();
                // 刷新表单
                this.$refs["dataForm"].resetFields();
            }
            this.getList();
        },
        // 隐藏表单
        hideForm() {
            // 更改值
            this.formVisible = false;
            return true;
        },
        formatStatus(row) {
            switch (row.status) {
                case true:
                    return "通过";
                case false:
                    return "失败";
                default:
                    return "失败";
            }
        }
    },
    filters: {},
    mounted() {
        document.body.ondrop = function(event) {
            event.preventDefault();
            event.stopPropagation();
        };
    },
    created() {
        // 将参数拷贝进查询对象
        let query = this.$route.query;
        this.query = Object.assign(this.query, query);
        this.query.limit = parseInt(this.query.limit);
        // 加载表格数据
        this.getList();
    }
};
</script>

<style type="text/scss" lang="scss">
</style>
