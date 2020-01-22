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
                    <el-button type="primary" @click.native="handleForm(null,null)">新增</el-button>
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
                width="40"
                fixed>
            </el-table-column>
            <el-table-column
                label="帐号"
                prop="username"
                fixed>
            </el-table-column>
            <el-table-column
                label="装机上限"
                prop="installLimit"
                width="80"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="未分配余额"
                prop="balance"
                width="90"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="证书"
                prop="cert"
                width="380"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="绑定应用"
                prop="appName"
                width="80"
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
                    <el-button type="text" size="small" @click.native="resetInstallForm(scope.$index, scope.row)">重置</el-button>
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
            width="40%"
            top="5vh">
            <el-form  :model="formData" :rules="formRules" ref="dataForm">
                <el-form-item label="开发者帐号" prop="username">
                    <el-input v-model="formData.username" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="formData.password" auto-complete="off" show-password></el-input>
                </el-form-item>
                <el-form-item label="证书名称" prop="cert">
                    <el-input v-model="formData.cert" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="装机数量" prop="installLimit">
                    <el-input-number v-model="formData.installLimit" :step="50"></el-input-number>
                </el-form-item>
            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button @click.native="hideForm">取消</el-button>
                <el-button type="primary" @click.native="formSubmit()" :loading="formLoading">提交</el-button>
            </div>
        </el-dialog>

        <!--表单-->
        <el-dialog
            title="重置装机数量"
            :visible.sync="resetInstallVisible"
            :before-close="hideForm"
            width="40%"
            top="5vh">
            <div class="page">
                <span>开发者帐号:{{formData.username}}</span>
            </div>

            <el-form  :model="formData" :rules="formRules" ref="dataForm">
                <el-form-item label="装机数量" prop="installLimit">
                    <el-input-number v-model="formData.installLimit" :step="50"></el-input-number>
                </el-form-item>
            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button @click.native="hideForm">取消</el-button>
                <el-button type="primary" @click.native="resetInstallSubmit()" :loading="formLoading">提交</el-button>
            </div>
        </el-dialog>

    </div>

</template>

<script>
import {
    developerList,
    addDeveloper,
    resetInstall
} from "../../api/developer/developer";
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
            formMap: {
                add: "新增",
                edit: "编辑"
            },
            formLoading: false,
            formVisible: false,
            resetInstallVisible: false,
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
            developerList(this.query)
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
            this.resetInstallVisible = false;
            return true;
        },
        resetInstallForm(index, row) {
            this.resetInstallVisible = true;
            this.formData = row;
        },
        resetInstallSubmit() {
            this.$refs["dataForm"].validate(valid => {
                if (valid) {
                    this.formLoading = true;
                    let data = JSON.parse(JSON.stringify(formJson));
                    data.id = this.formData.id;
                    data.installLimit = this.formData.installLimit;
                    resetInstall(data)
                        .then(response => {
                            this.formLoading = false;
                            if (response.code) {
                                this.$message.error(response.message);
                                return false;
                            }
                            this.$message.success("操作成功");
                            this.hideForm();
                            this.resetForm();
                        })
                        .catch(() => {
                            this.formLoading = false;
                        });
                }
            });
        },
        // 显示表单
        handleForm(index, row) {
            this.appInfo = {};
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
        },
        formSubmit() {
            this.$refs["dataForm"].validate(valid => {
                if (valid) {
                    this.formLoading = true;
                    addDeveloper(this.formData)
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
                }
            });
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
