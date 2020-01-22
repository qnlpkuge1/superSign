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
                width="120"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="安装上限"
                prop="installLimit"
                width="120"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="可用安装量"
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
                    <el-button type="text" size="small" @click.native="showAppinfo(scope.$index, scope.row)">查看应用地址</el-button>
                    <el-button type="text" size="small" @click.native="updateAppinfo(scope.$index, scope.row)">更新程序包</el-button>
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
            <div class="page" v-if="!appInfo.appName">
                <h4>上传未签名的IPA：</h4>
                <el-card class="box-card">
                    <upload :size="600000000" @on-select="onSelectIpa"></upload>
                </el-card>
            </div>
            <el-container v-if="appInfo.appName">
              <el-container>
                <el-aside width="180px">
                    <img v-bind:src="appInfo.fullIconPath">
                </el-aside>
                <el-container>
                  <el-main>
                        <el-row :gutter="20">
                          <el-col :span="24"><div class="grid-content bg-purple"><B>应用名称: </B>{{ appInfo.appName }}</div></el-col>
                        </el-row>
                        <el-row :gutter="20">
                          <el-col :span="24"><div class="grid-content bg-purple"><B>版本号: </B>{{ appInfo.version }}</div></el-col>
                        </el-row>
                        <el-row :gutter="20">
                          <el-col :span="24"><div class="grid-content bg-purple"><B>bundleID: </B>{{ appInfo.appIdReal }}</div></el-col>
                        </el-row>
                  </el-main>
                </el-container>
              </el-container>
            </el-container>
            <el-form  v-if="appInfo.appName" :model="formData" :rules="formRules" ref="dataForm">
                <el-form-item label="装机数量" prop="installLimit">
                    <el-input-number v-model="formData.installLimit" :step="100"></el-input-number>
                </el-form-item>
            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button @click.native="hideForm">取消</el-button>
                <el-button type="primary" @click.native="formSubmit()" :loading="formLoading">提交</el-button>
            </div>
        </el-dialog>

        <!--更新程序包-->
        <el-dialog
            title="更新程序包"
            :visible.sync="updateFormVisible"
            :before-close="hideForm"
            width="40%"
            top="5vh">
            <div class="page">
                <h4>上传未签名的IPA， 程序包更新后5分钟生效，生效后将覆盖原来的版本：</h4>
                <el-card class="box-card">
                    <uploadUpgrade :size="600000000" :appInfoId="appInfo.id" @on-select="onSelectUpgradeIpa"></uploadUpgrade>
                </el-card>
            </div>

            <div slot="footer" class="dialog-footer">
                <el-button @click.native="hideForm">关闭</el-button>
            </div>
        </el-dialog>

        <el-dialog
            title="应用详情地址"
            :visible.sync="urlDialogVisible"
            :before-close="hideForm"
            width="40%"
            top="5vh">
            <div class="page" >
                {{appInfoUrl}}
            </div>
        </el-dialog>

    </div>

</template>

<script>
import { appSiteList, setInstallLimit, getUrl } from "../../api/appInfo/appSite";
import Upload from "../../components/File/UploadIpa.vue";
import uploadUpgrade from "../../components/File/UploadUpgradeIpa.vue";

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
            urlDialogVisible: false,
            appInfoUrl: "",
            formLoading: false,
            formVisible: false,
            updateFormVisible: false,
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
    components: {
        Upload,uploadUpgrade
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
        onSelectIpa(appInfo) {
            this.appInfo = appInfo;
        },
        onSelectUpgradeIpa(appInfo) {
            this.appInfo = appInfo;
            this.$message.success("更新成功,5分钟后生效");
            this.hideForm();
            this.getList();
        },
        handleCurrentChange(val) {
            this.query.page = val;
            this.getList();
        },
        getList() {
            this.loading = true;
            appSiteList(this.query)
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
            this.urlDialogVisible = false;
            this.updateFormVisible = false;
            return true;
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
        showAppinfo(index, row) {
            let data = JSON.parse(JSON.stringify(formJson));
            data.id = row.id;
            getUrl(data)
                .then(response => {
                    this.urlDialogVisible = true;
                    this.appInfoUrl = response.data;
                })
                .catch(() => {
                    this.formLoading = false;
                });
        },
        updateAppinfo(index, row) {
            this.updateFormVisible = true;
            this.appInfo = row;
        },
        formSubmit() {
            this.$refs["dataForm"].validate(valid => {
                if (valid) {
                    this.formLoading = true;
                    let data = Object.assign({}, this.formData);
                    data.id = this.appInfo.id;

                    setInstallLimit(data)
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
