<template>

    <div>
        <el-form :inline="true" :model="query" class="query-form" size="mini">
            <el-form-item class="query-form-item">
                <el-input v-model="query.appName" placeholder="应用名称"></el-input>
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
                label="应用名称"
                prop="appName">
            </el-table-column>
            <el-table-column
                label="设备型号"
                prop="product"
                width="120"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="设备id"
                prop="udid"
                width="380"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="扣除装机量"
                prop="deduction"
                width="100"
                :formatter="formatDeduction"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="开发者帐号"
                prop="developerName"
                width="200"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="安装时间"
                prop="updateTime">
                <template slot-scope="scope">
                    <i class="el-icon-time"></i>
                    <span>{{ scope.row.createTime | formatDateStr('yyyy-MM-dd hh:mm:ss') }}</span>
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
import { installList } from "../../api/appInfo/installLog";

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
            loading: true
        };
    },
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
            installList(this.query)
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
        formatDeduction(row) {
            switch (row.deduction) {
                case -1:
                    return "";
                default:
                    return row.deduction;
            }
        }
    },
    filters: {},
    mounted() {
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
