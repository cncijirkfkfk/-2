# 动漫共和国 · CloudStream 茶杯狐首批插件项目

## 本次范围
- 原始源顺序第 1 项：茶杯狐
- 原始入口：`https://cupfox.app/`
- CloudStream 插件模块：`CupFox`
- 目标产物：CloudStream `.cs3` 插件

## 重要功能边界
根据本次检查到的茶杯狐首页，它目前呈现的是电影推荐/片单/文章目录。原始 44 源 ZIP 只提供网站名称和入口 URL，没有搜索接口、详情 DOM 结构、剧集 API 或播放器规则。

因此本首版是**安装与网页目录适配测试版**：
- 读取首页同域链接并显示为 CloudStream 首页条目；
- 可打开条目页面并读取标题、描述和封面元数据；
- 搜索暂未实现（没有确认搜索接口）；
- 视频播放暂未实现（未确认可合法使用的播放流地址）。

它不是已经完成的影视播放源，不会把普通网页地址伪装成视频流。

## 构建
需要 JDK 17、Gradle 8.9 和可访问 Google Maven Central/JitPack 的网络环境：

```bash
gradle :CupFox:make
```

CloudStream Gradle 插件会在模块构建目录生成 `.cs3` 文件。GitHub Actions 工作流也会自动构建并上传产物。

## GitHub Actions 发布/测试
1. 在 GitHub 新建空仓库并上传本项目全部文件。
2. 打开仓库 `Settings → Actions → General`，允许 Actions。
3. `Settings → Actions → General → Workflow permissions` 允许读写权限（如后续要自动发布仓库清单）。
4. 打开 `Actions → Build CloudStream plugin`，运行工作流。
5. 成功后从运行记录的 Artifacts 下载 `CupFox-cs3`。

## 在 CloudStream 官方版本地测试
将构建出的 `.cs3` 文件放入手机可访问的目录，再按 CloudStream 当前版本支持的本地插件安装入口导入。Android 11+ 本地测试可能需要授予 CloudStream“所有文件访问”权限。更稳定的分发方式是把构建产物发布到插件仓库。

## 来源与参考
- 原始输入：`DongmanRepublic_44_WebSources.zip` 中 `app/src/main/assets/sources.json` 的第 1 项。
- CloudStream 开发文档：https://recloudstream.github.io/csdocs/devs/gettingstarted/
- 官方测试插件模板：https://github.com/recloudstream/TestPlugins
