<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 文章状态 -->
    <div class="status-menu">
      <span>状态</span>
      <span @click="changeStatus(null)" :class="isActive(null)">全部</span>
      <span @click="changeStatus(1)" :class="isActive(1)"> 公开 </span>
      <span @click="changeStatus(2)" :class="isActive(2)"> 私密 </span>
    </div>
    <el-empty v-if="talkList.length == 0" description="暂无说说"></el-empty>
    <!-- 说说列表 -->
    <div class="talk-item" v-for="item of talkList" :key="item.id">
      <!-- 用户信息 -->
      <div class="user-info-wrapper">
        <el-avatar class="user-avatar" :src="item.avatar" :size="36"></el-avatar>
        <div class="user-detail-wrapper">
          <div class="user-nickname">
            <div>{{ item.nickname }}</div>
            <!-- 操作 -->
            <el-dropdown trigger="click" @command="handleCommand">
              <i class="el-icon-more" style="color: #333; cursor: pointer"></i>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :command="'1,' + item.id">
                  <i class="el-icon-edit"></i>编辑
                </el-dropdown-item>
                <el-dropdown-item :command="'2,' + item.id">
                  <i class="el-icon-delete"></i>删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <!-- 发表时间 -->
          <div class="time">
            <span v-if="item.isTime == 0">{{ item.createTime | dateTime }}</span>
            <span v-if="item.isTime == 1">预计于 {{ item.createTime | dateTime }} 发布</span>
            <span class="top" v-if="item.isTop == 1">
              <i class="el-icon-collection-tag"></i> 置顶
            </span>
            <span class="secret" v-if="item.status == 2"> <i class="el-icon-view"></i> 私密 </span>
          </div>
          <!-- 说说信息 -->
          <div class="talk-content" v-html="item.talkContent"></div>
          <!-- 图片列表 -->
          <el-row :gutter="4" class="talk-images" v-if="item.imgList">
            <el-col :md="8" :cols="6" v-for="(img, index) of item.imgList" :key="index">
              <el-image class="images-items" :src="img" :preview-src-list="previewList"></el-image>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    <!-- 分页 -->
    <el-pagination
      :hide-on-single-page="false"
      class="pagination-container"
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="count"
      layout="prev, pager, next"
    ></el-pagination>
    <!-- 删除对话框 -->
    <el-dialog :visible.sync="isDelete" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color: #ff9900"></i>提示
      </div>
      <div style="font-size: 1rem">是否删除该说说？</div>
      <div slot="footer">
        <el-button @click="isDelete = false">取 消</el-button>
        <el-button type="primary" @click="deleteTalk"> 确 定 </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import { getTalks, deleteTalk } from "@/api/talk";
export default {
  name: "TalkList",
  created() {
    this.listTalks();
  },
  data: function () {
    return {
      currentPage: 1,
      pageSize: 5,
      count: 0,
      isTop: null,
      status: null,
      isDelete: false,
      talkList: [],
      previewList: [],
      talkId: null,
    };
  },
  methods: {
    handleCommand(command) {
      var arr = command.split(",");
      this.talkId = arr[1];
      switch (arr[0]) {
        case "1":
          this.$router.push({ path: `/talks/${this.talkId}` });
          break;
        case "2":
          this.isDelete = true;
          break;
      }
    },
    listTalks() {
      getTalks(this.currentPage, this.pageSize, this.status).then(({ data }) => {
        this.talkList = data.data.recordList;
        this.talkList.forEach((item) => {
          if (item.imgList) {
            this.previewList.push(...item.imgList);
          }
        });
        this.count = data.data.count;
      });
    },
    sizeChange(pageSize) {
      this.previewList = [];
      this.pageSize = pageSize;
      this.listTalks();
    },
    currentChange(currentPage) {
      this.previewList = [];
      this.currentPage = currentPage;
      this.listTalks();
    },
    changeStatus(status) {
      this.currentPage = 1;
      this.previewList = [];
      this.status = status;
      this.listTalks();
    },
    deleteTalk() {
      deleteTalk(this.talkId).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.msg,
          });
          this.listTalks();
        }
        this.isDelete = false;
      });
    },
  },
  computed: {
    isActive() {
      return function (status) {
        return this.status == status ? "active-status" : "status";
      };
    },
  },
};
</script>

<style scoped>
.status-menu {
  font-size: 14px;
  margin-top: 40px;
  color: #999;
}
.status-menu span {
  margin-right: 24px;
}
.status {
  cursor: pointer;
}
.active-status {
  cursor: pointer;
  color: #333;
  font-weight: bold;
}
.talk-item:not(:first-child) {
  margin-top: 20px;
}
.talk-item {
  padding: 16px 20px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 3px 8px 6px rgb(7 17 27 / 6%);
  transition: all 0.3s ease 0s;
}
.talk-item:hover {
  box-shadow: 0 5px 10px 8px rgb(7 17 27 / 16%);
  transform: translateY(-3px);
}
.user-info-wrapper {
  width: 100%;
  display: flex;
}
.user-avatar {
  border-radius: 50%;
}
.user-avatar {
  transition: all 0.5s;
}
.user-avatar:hover {
  transform: rotate(360deg);
}
.user-detail-wrapper {
  margin-left: 10px;
  width: 100%;
}
.user-nickname {
  font-size: 15px;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
}
.user-sign {
  margin-left: 4px;
}
.time {
  color: #999;
  margin-top: 2px;
  font-size: 12px;
}
.top {
  color: #ff7242;
  margin-left: 10px;
}
.secret {
  color: #999;
  margin-left: 10px;
}
.talk-content {
  margin-top: 8px;
  font-size: 14px;
  white-space: pre-line;
  word-wrap: break-word;
  word-break: break-all;
}
.talk-images {
  margin-top: 8px;
}
.images-items {
  cursor: pointer;
  object-fit: cover;
  height: 200px;
  width: 100%;
  border-radius: 4px;
}
</style>
