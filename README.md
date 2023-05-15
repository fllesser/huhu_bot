# huhu_bot
nonebot2, 但是jvav

## 介绍
1. java(springboot)基于go-cqhttp, websocket反向连接的qq机器人(机器人:ws服务端,gocq:ws客户端);
2. 支持被多个go-cqhttp连接;

## 架构
Spring Boot
go-cqhttp
websocket

## 使用要点
go-cqhttp 反向ws地址设置如 ws://127.0.0.1:8888/huhu/ws


参考 [haruhibot](https://gitee.com/Lelouch-cc/haruhibot-server)
