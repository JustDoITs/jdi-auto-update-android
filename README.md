# jdi-auto-update-android
auto update for android

## 目录

* [功能介绍](#功能介绍)
* [Gradle 依赖](#Gradle依赖)
* [简单使用](#简单使用)
* [License](#license)

## 功能介绍

- [x] 实现android版本更新
- [x] 支持后台下载
- [x] 支持静默下载（可以设置wifi状态下）
- [x] 支持android7.0


## 服务端

```json
{
  "versionCode": 2,
  "versionName": "0.2.1",
  "url": "http://www.your-website.com/xxx-100-20170803-03_18_13.apk",
  "msg": "1，添加删除信用卡接口。\r\n2，添加vip认证。\r\n3，区分自定义消费，一个小时不限制。\r\n4，添加放弃任务接口，小时内不生成。\r\n5，消费任务手动生成。",
  "size": "1024",
  "md5":"db2e7e39fe3d5501c9b64fce2865f73d"
}
```
