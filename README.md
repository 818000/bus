<p align="center">
	<a href="https://www.aoju.org"><img src="https://cdn.macaws.cn/BUS_800x300.png" width="45%"></a>
</p>
<p align="center">
	<a href="http://www.aoju.org">http://www.aoju.org</a>
</p>
<p align="center">
    <a target="_blank" href="https://search.maven.org/search?q=org.aoju">
		<img src="https://img.shields.io/badge/maven--central-v6.1.8-blue.svg?label=Maven%20Central" />
	</a>
	<a target="_blank" href="https://travis-ci.org/aoju/bus">
		<img src="https://travis-ci.org/aoju/bus.svg?branch=master">
	</a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
		<img src="https://img.shields.io/badge/JDK-1.8+-green.svg">
	</a>
	<a target="_blank" href="https://spring.io/projects/spring-boot">
		<img src="https://img.shields.io/badge/Spring Boot-2.3.0-brightgreen.svg">
	</a>
	<a target="_blank" href="https://www.mysql.com">
		<img src="https://img.shields.io/badge/Mysql-5.7-blue.svg">
	</a>
	<a target="_blank" href="http://dubbo.apache.org">
		<img src="https://img.shields.io/badge/dubbo-2.7.5-yellow.svg">
	</a>
	<a target="_blank" href="http://poi.apache.org">
		<img src="https://img.shields.io/badge/poi-4.1.2-blue.svg">
	</a>
	<a target="_blank" href="https://opensource.org/licenses/MIT">
		<img src="https://img.shields.io/badge/license-MIT-green.svg">
	</a>
</p>

<p align="center">
	-- QQ群①：839128 --
</p>

---

## 项目说明

Bus (应用/服务总线) 是一个微服务套件、基础框架，它基于Java8编写，参考、借鉴了大量已有框架、组件的设计，可以作为后端服务的开发基础中间件。代码简洁，架构清晰，非常适合学习使用。

很开心地告诉大家这套组件上手和学习难度非常小。如果是以学会使用为目的，只要你会Java语言即可。之前做项目的时候，往往会遇到各种各样的问题，这些问题有可能是会遇到很多次，不善于总结沉淀，这是很多人的一个通病，包括我自己也是。

于是我就萌生了把这些问题沉淀成组件的想法，分享自己成长路线,当然也参考了部分开源项目，资料，文章进行整合的一个提供基础功能的项目。 本项目旨在实现基础能力，不设计具体业务，希望能帮助到大家，也让大家见证我的勤奋与努力，一起进步。

欢迎大家来 这里 踩踩,生命有限！少写重复代码！给颗星奖励下呗~

目标期望能努力打造一套从 基础框架 - 分布式微服务架构 - 持续集成 - 自动化部署 -系统监测等，快速实现业务需求的全栈式技术解决方案。

## 组件信息

| 完成 |模块 | 描述信息 |
|------| ------ | ------- |
|[ √ ]|bus-all|为微服务提供统一的pom管理，以及通用组件| 
|[ √ ]|bus-base|基础功能及base相关功能,实体类(Entity),服务(Service),接口(Controller)|
|[ √ ]|bus-bom|包含所有组建信息,当然可以通过配置按需加载等|
|[ × ]|bus-cache|缓存服务及工具,支持redis,memcached,ehcache,hession等|
|[ √ ]|bus-core|核心功能及工具类,包括常量、线程、类加载器、反射、集合、日期等常用工具|
|[ √ ]|bus-cron|定时器及定时任务等功能|
|[ √ ]|bus-crypto|加密解密，支持支持AES/DES/REA/MD5等常用加密算法|
|[ √ ]|bus-extra|扩展功能及文件操作,FTP/文件/二维码/短信相关支持|
|[ √ ]|bus-health|应用服务器健康信息，软件硬件信息采集等|
|[ √ ]|bus-http|HTTP功能封装,根据业务场景可使用 Httpd/Httpx/Httpz 不同的处理方式|
|[ √ ]|bus-image|影像应用服务，解析预览等|
|[ × ]|bus-limiter|请求限流,根据不同业务设置不同限流策略|
|[ √ ]|bus-logger|日志信息及功能，动态检测日志实现的方式，使日志使用个更加便利灵活简单|
|[ √ ]|bus-mapper|数据操作,在 MyBatis 的基础上只做增强不做改变，为简化开发、提高效率而生|
|[ √ ]|bus-oauth|第三方登录,已集成十多家第三方平台(国内外常用的基本都已包含)|
|[ √ ]|bus-pager|数据分页,mybatis|
|[ √ ]|bus-office|office等相关转换及处理,POI封装实现，使Java操作Excel等文件变得更加简单|
|[ √ ]|bus-opencv|图像识别及分析，提供丰富的图形图像处理算法,跨平台支持等|
|[ √ ]|bus-proxy|公共代理，使动态代理变得简单|
|[ √ ]|bus-sensitive|敏感数据脱敏,对应用和使用者透明，业务逻辑无感知，通过配置集成，改动代码量小|
|[ √ ]|bus-setting|设置工具类， 用于支持设置/配置|
|[ √ ]|bus-shade|Entity,Service,Mapper等相关代码生成工具|
|[ × ]|bus-socket|基础NIO/AIO通讯,Socket封装，支持TCP/UDP服务端|
|[ √ ]|bus-starter|SpringBoot starter，spring相关配置，启动及相关配置文件信息|
|[ √ ]|bus-storage|文件存储组件,,支持阿里云、七牛，提供了工厂模式和注入两种集成方式|
|[ × ]|bus-tracer|轻量级分布式链路跟踪监控，日志及访问流程追踪以及内部调用链追踪|
|[ √ ]|bus-validate|参数校验，会默认拦截所有的标记有`@Valid`的方法或类|

## 功能概述

#### 功能概述

1. Java基础工具类，对文件、流、加密解密、转码、正则、线程、XML等JDK方法进行封装，组成各种工具类； 以及结合springboot封装常用工具按需加载例如mybatis、xss、i18n、sensitive、validate等框架

2. 详细说明以及使用姿势请参考每个模块下README介绍

## 安装使用

### Maven

```
<dependency>
    <groupId>org.aoju</groupId>
    <artifactId>bus-all</artifactId>
    <version>6.1.8</version>
</dependency>
```

或者单独使用某个组件

```
<dependency>
    <groupId>org.aoju</groupId>
    <artifactId>bus-xxx</artifactId>
    <version>x.x.x</version>
</dependency>
```

### Gradle

```
implementation 'org.aoju:bus-all:6.1.8'
```

### Downlad

点击以下任一链接，下载`bus-*-x.x.x.jar`即可：

- [Maven中央库](https://repo1.maven.org/maven2/org/aoju)

### 测试&使用

为确保项目编译效率及相关规则，本项目所有单元测试及使用请参考`abarth`项目：

- 地址: [https://github.com/aoju/abarth](https://github.com/aoju/abarth)

> 注意
> Bus项目支持JDK8+，对Android平台部分模块没有测试，不能保证所有工具类或工具方法可用。

## 分支说明

源码分为两个分支，功能如下：

| 分支       | 作用                                                          |
|-----------|---------------------------------------------------------------|
| master    | 主分支，即稳定版本使用的分支，与中央库提交的jar一致，不接收任何PR或修改 |
| develop   | 开发分支，默认为下个版本的更新或者修复等，接受修改或PR             |

## 意见建议

All kinds of contributions (enhancements, new features, documentation & code improvements, issues & bugs reporting) are
welcome.

欢迎各种形式的贡献，包括但不限于优化，添加功能，文档 & 代码的改进，问题和 BUG 的报告。

### Issue:

- [版本情况]：jdk-openjdk_8_201 bus-xxx-6.x.x（请确保最新版本尝试是否还存在问题）
- [问题描述]：（包括截图）
- [复现代码]：
- [堆栈信息]：

### Features:

- [增加功能]： 内容
- [修改描述]： 内容

注意：测试涉及到的文件请脱敏

## 许可证(license)

### MIT

Open sourced under the MIT license.

根据 MIT 许可证开源。

### JetBrains

Thanks JetBrains for the OpenSource license.

感谢JetBrains提供IDEA免费license

##

源码永远是最好的教程，善于读源码和DEBUG朋友掌握完全是轻而易举的事。源码是作者设计理念最直观的展现，这也是开源的魅力所在。"Talk is cheap, Show me the code."
，开源让技术难题的探讨变得更加务实，在您看完源码后心中对它都会有一个定论。在作者看来，Bus切切实实降低了开发学习门槛，也保障了服务的高性能、高可用。如果读者朋友对源码中某些部分的设计存在疑虑，也欢迎与作者保持沟通。
