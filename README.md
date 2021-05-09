# TBSpider
基于Jsoup实现的爬虫demo项目，支持对淘宝商品页面进行抓取分析

## 框架和技术

- JDK-1.8
- SpringBoot-2.4.5
- Jsoup-1.13.1
- poi-5.0.0
- bootstrap-3.3.5 bootstrap-fileinput-4.3.1

## 功能介绍
启动SpiderApplication后，页面访问地址为[http://localhost:8888]()，选择需要分析的淘宝链接excel进行上传，后台收到文件会解析Excel并逐行对链接分析商品当前是否是出售中状态，分析完成后支持下载出售中的商品链接excel。

因为本项目是单机版，所以文件都存储在临时文件夹，进度也是保存再内存中，重启以后会丢失所有数据，需要分布式或者持久化的话可以自行改造。
