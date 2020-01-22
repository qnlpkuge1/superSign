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
                prop="name"
                fixed>
            </el-table-column>
            <el-table-column
                label="类型"
                prop="type"
                width="150"
                :formatter="formatType"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="状态"
                prop="status"
                :formatter="formatStatus"
                width="150"
                :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column
                label="创建时间"
                prop="createTime">
                <template slot-scope="scope">
                    <i class="el-icon-time"></i>
                    <span>{{ scope.row.createTime | formatDateStr('yyyy-MM-dd hh:mm:ss') }}</span>
                </template>
            </el-table-column>
            <el-table-column
                label="操作"
                fixed="right">
                <template slot-scope="scope">
                    <el-button type="text" size="small" @click.native="showAppinfo(scope.$index, scope.row)">应用地址</el-button>
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
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload">
              <img v-if="formData.iconPath" :src="formData.iconPath" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <el-form  :model="formData" :rules="formRules" ref="dataForm">
                <el-form-item label="应用名称" prop="installLimit">
                    <el-input v-model="formData.name"></el-input>
                </el-form-item>
                <el-form-item label="封包网址" prop="url">
                    <el-input v-model="formData.url"></el-input>
                </el-form-item>
                <el-radio-group v-model="formData.type" >
                  <el-radio-button label="normal">普通</el-radio-button>
                  <el-radio-button label="removePassword">带密码删除</el-radio-button>
                  <el-radio-button label="unRemove">不可删除</el-radio-button>
                </el-radio-group>

                <el-form-item label="密码" v-if="formData.type=='removePassword'" prop="removePassword">
                    <el-input v-model="formData.removePassword"  show-password></el-input>
                </el-form-item>

            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button @click.native="hideForm">取消</el-button>
                <el-button type="primary" @click.native="formSubmit()" :loading="formLoading">提交</el-button>
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
import { pkgAppList, addPkgApp, getUrl } from "../../api/pkgApp/pkgApp";
import { BASE_URL } from "../../config/app";
import Upload from "../../components/File/UploadIpa.vue";
const formJson = {
    id: null,
    name: "",
    iconPath: "",
    url: "",
    type: "normal",
    removePassword: ""
};
export default {
    data() {
        return {
            query: {
                page: 1,
                limit: 20
            },
            uploadUrl: BASE_URL + "/admin/file/upload/icon",
            list: [],
            pkgApp: {},
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
            formData: formJson,
            ipaFilePath: "",
            ipaFilePathUrl: "",
            formRules: {
                name: [
                    {
                        required: true,
                        message: "请输入应用名称",
                        trigger: "blur"
                    }
                ]
            },
            deleteLoading: false
        };
    },
    components: {
        Upload
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
        handleAvatarSuccess(res) {
            this.formData.iconPath = res.data;
        },
        beforeAvatarUpload(file) {
            const isJPG = file.type === 'image/png';
            const isLt2M = file.size / 1024 / 1024 < 2;

            if (!isJPG) {
              this.$message.error('应用图标只能是 png 格式!');
            }
            if (!isLt2M) {
              this.$message.error('应用图标大小不能超过 2MB!');
            }
            return isJPG && isLt2M;
        },
        getList() {
            this.loading = true;
            pkgAppList(this.query)
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
            return true;
        },
        // 显示表单
        handleForm(index, row) {
            this.pkgApp = {};
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
        formSubmit() {
            this.$refs["dataForm"].validate(valid => {
                if (valid) {
                    this.formLoading = true;
                    let data = Object.assign({}, this.formData);
                    addPkgApp(data)
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
        },
        formatType(row) {
            switch (row.type) {
                case "normal":
                    return "普通";
                case "unRemove":
                    return "不可删除";
                case "removePassword":
                    return "带密码删除";
                default:
                    return "普通";
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

<style type="text/scss">
.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>
