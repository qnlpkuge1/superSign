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
                fixed>
            </el-table-column>
            <el-table-column
                label="应用名称"
                prop="appName"
                fixed>
            </el-table-column>
            <el-table-column
                label="版本"
                prop="version"
                width="150"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="状态"
                prop="status"
                :formatter="formatStatus"
                width="80"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="装机上限"
                prop="installLimit"
                width="100"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="可用装机量"
                prop="balance"
                width="100"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="创建时间"
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
                    <el-button type="text" size="small" @click.native="handleForm(scope.$index, scope.row)">分配</el-button>
                    <el-button v-if="scope.row.status !='1'" type="text" size="small" @click.native="handleEnableAppInfo(scope.$index, scope.row)">启用</el-button>
                    <el-button v-if="scope.row.status =='1'" type="text" size="small" @click.native="handleDisableAppInfo(scope.$index, scope.row)">下架</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination
            :page-size="query.limit"
            @current-change="handleCurrentChange"
            layout="prev, pager, next"
            :total="total">
        </el-pagination>

        <!--表单-->
        <el-dialog
            :title="formMap[formName]"
            :visible.sync="formVisible"
            :before-close="hideForm"
            width="60%"
            top="5vh">
            <el-table
                ref="multipleTable"
                :data="developerList"
                tooltip-effect="dark"
                style="width: 100%"
                @selection-change="handleSelectionChange">
                <el-table-column
                  type="selection"
                  width="55">
                </el-table-column>
                <el-table-column
                  prop="username"
                  label="帐号"
                  width="180">
                </el-table-column>
                <el-table-column
                    label="装机上限"
                    prop="installLimit"
                    with="60"
                    :show-overflow-tooltip="true">
                </el-table-column>
                <el-table-column
                    label="创建时间"
                    prop="updateTime">
                    <template slot-scope="scope">
                        <i class="el-icon-time"></i>
                        <span>{{ scope.row.createTime | formatDateStr('yyyy-MM-dd hh:mm:ss') }}</span>
                    </template>
                </el-table-column>
              </el-table>

            <div slot="footer" class="dialog-footer">
                <el-button @click.native="hideForm">取消</el-button>
                <el-button type="primary" @click.native="formSubmit()" :loading="formLoading">提交</el-button>
            </div>
        </el-dialog>

    </div>

</template>

<script>
import {
    appSiteAllList,
    enableAppInfo,
    disableAppInfo
} from "../../api/appInfo/appSite";
import { developerListUnallocat } from "../../api/developer/developer";
import { allocatDeveloper } from "../../api/developer/developerAllocat";
const formJson = {
    developerIds: [],
    appInfoId: ""
};

const simpleFormJson = {
    id: ""
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
            formMap: {
                add: "分配",
                edit: "分配"
            },
            multipleSelection: [],
            developerList: [],
            formLoading: false,
            formVisible: false,
            formData: formJson,
            ipaFilePath: "",
            ipaFilePathUrl: "",
            formRules: {
                installLimit: [
                    {
                        required: true,
                        message: "请输入装机数量",
                        trigger: "blur"
                    }
                ]
            },
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
        onSelectIpa(appInfo) {
            this.appInfo = appInfo;
        },
        handleCurrentChange(val) {
            this.query.page = val;
            this.getList();
        },
        getList() {
            this.loading = true;
            appSiteAllList(this.query)
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
        getDeveloperList() {
            this.loading = true;
            developerListUnallocat()
                .then(response => {
                    this.loading = false;
                    this.developerList = response.data || [];
                })
                .catch(() => {
                    this.loading = false;
                    this.developerList = [];
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
            this.formVisible = !this.formVisible;
            return true;
        },
        handleEnableAppInfo(index, row) {
            let data = JSON.parse(JSON.stringify(simpleFormJson));
            data.id = row.id;
            enableAppInfo(data)
                .then(response => {
                    if (response.code) {
                        this.$message.error(response.message);
                        return false;
                    }
                    this.$message.success("操作成功");
                })
                .catch(() => {
                    this.formLoading = false;
                });
            this.getList();
        },
        handleDisableAppInfo(index, row) {
            let data = JSON.parse(JSON.stringify(simpleFormJson));
            data.id = row.id;
            disableAppInfo(data)
                .then(response => {
                    if (response.code) {
                        this.$message.error(response.message);
                        return false;
                    }
                    this.$message.success("操作成功");
                })
                .catch(() => {
                    this.formLoading = false;
                });
            this.getList();
        },
        // 显示表单
        handleForm(index, row) {
            this.appInfo = row;
            this.formVisible = true;
            this.formData = JSON.parse(JSON.stringify(formJson));
            if (row !== null) {
                this.formData = Object.assign({}, row);
            }
            this.formName = "add";
            if (index !== null) {
                this.index = index;
                this.formName = "edit";
            }
            this.getDeveloperList();
        },
        formSubmit() {
            var developerIds = [];
            this.multipleSelection.forEach((item, index) => {
                developerIds[index] = item.id;
            });

            let data = JSON.parse(JSON.stringify(formJson));
            data.developerIds = developerIds;
            data.appInfoId = this.appInfo.id;
            console.log(data);
            this.formLoading = true;
            allocatDeveloper(data)
                .then(response => {
                    this.formLoading = false;
                    if (response.code) {
                        this.$message.error(response.message);
                        return false;
                    }
                    this.$message.success("操作成功");
                    this.formVisible = false;
                    this.resetForm();
                })
                .catch(() => {
                    this.formLoading = false;
                });
        },
        formatStatus(row) {
            switch (row.status) {
                case 1:
                    return "启用";
                case 0:
                    return "停用";
                case -1:
                    return "初始化";
                default:
                    return "停用";
            }
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
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
