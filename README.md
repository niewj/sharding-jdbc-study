# 介绍

## 1. 什么是Sharding Sphere

### 1.1 简介:摘自官网的介绍

> Apache ShardingSphere 是一套开源的分布式数据库中间件解决方案组成的生态圈，它由 `JDBC`、`Proxy` 和 Sidecar（规划中）这 3 款相互独立，却又能够混合部署配合使用的产品组成。 它们均提供标准化的`数据分片`、`分布式事务`和`数据库治理`功能，可适用于如 Java 同构、异构语言、云原生等各种多样化的应用场景。

> Apache ShardingSphere 定位为`关系型数据库`中间件，旨在充分合理地在分布式的场景下利用关系型数据库的计算和存储能力，而并非实现一个全新的关系型数据库。 它通过关注不变，进而抓住事物本质。关系型数据库当今依然占有巨大市场，是各个公司核心业务的基石，未来也难于撼动，我们目前阶段更加关注在原有基础上的增量，而非颠覆。

> Apache ShardingSphere 5.x 版本开始致力于`可插拔架构`，项目的功能组件能够灵活的以可插拔的方式进行扩展。 目前，数据分片、读写分离、多数据副本、数据加密、影子库压测等功能，以及 MySQL、PostgreSQL、SQLServer、Oracle 等 SQL 与协议的支持，均通过插件的方式织入项目。 开发者能够像使用积木一样定制属于自己的独特系统。Apache ShardingSphere 目前已提供数十个` SPI `作为系统的扩展点，仍在不断增加中。

ShardingSphere 已于2020年4月16日成为 Apache 软件基金会的顶级项目。

### 1.2 小结:

1. 分布式数据库中间件;
2. Sharding-JDBC和Sharding-Proxy;
3. 分库分表;

## 2 分库分表

### 2.1 分库分表-垂直分表

#### 1). 拆分要点

1. 字段拆分抽为新表; 
2. 拆分后各表 记录数和原数都一样;

#### 2). 拆分优点

 如果把原来`商品`信息表拆分成: `商品基本信息` 和 `商品扩展信息`两个表; 比如首页只展示商品基本信息,  只需要用到`商品基本信息`就可以了; 有修改的话, 也不用动另一个表;



### 2.2 分库分表-垂直分库

#### 1). 要点

按照业务,  专库专表; 

比如 拆出 `课程数据库`专门放课程表; `订单数据库`专门存放订单表;

#### 2). 优点

按照业务功能, 专库专用, 减少IO;

### 2.3 分库分表-水平分库

#### 1). 要点

相同的数据库, 只是标识不一样, 按照一定的选择算法, 决定数据定位到哪一个库;

#### 2). 优点

降低单库IO压力

### 2.4 分库分表-水平分表

#### 1). 要点

同一个数据库中同构表有多个,  存放数据不一样(结构一样)

比如: 有`订单表` , 同一个库里有1000张订单表 order1 order2 .....结构相同, 按照id的hash落到不同的表中; 

 比如每个库总量一亿条数据拆分到1000个表中, 每个表10w记录;

#### 2). 优点

减少单库单表的数据, 并发环境下带来的请求压力;

### 2.5 小结和问题

#### 1) 表结构设计时:

设计表时就要考虑 垂直分库和垂直分表

#### 2). 随着数据量的激增

数据激增时, 先不要急于做水平拆分, 应该先尝试: 缓存/读写分离/使用索引等方式, 这些方式解决不了问题了, 再做水平分库和水平分表的考虑;

#### 3) 分库分表带来的问题

##### a. 跨节点连接查询问题

举例: 查询关联操作时, 关联的可能是拆分的不同库不同表(水平拆库和水平拆表后的)分多次查询才能拿到的数据, 如果再加上分页/排序等, 导致关联难度剧增;

##### b. 多数据源管理问题

## 3. Sharding JDBC 分库分表

### 3.1 简介

轻量级的Java框架, 增强版的JDBC驱动

### 3.2 目的

主要目的是: 简化对分库分表之后的数据相关操作;

### 3.3 实例:

#### (1). 依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <!-- shardingjdbc依赖 -->
    <dependency>
        <groupId>org.apache.shardingsphere</groupId>
        <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
        <version>4.0.1</version>
    </dependency>

    <!-- mybatisplus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.0.5</version>
    </dependency>

    <!-- druid连接池依赖 -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.20</version>
    </dependency>

    <!-- mysql连接池依赖 -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>org.junit.vintage</groupId>
                <artifactId>junit-vintage-engine</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```

#### (2). 按照水平分表方式, 建表建库

创建 order_db

数据库创建两张表 order_1e和order_2

约定 订单id后缀为奇数入 order_1, 为偶数, 入 order_2



## 4. Sharding Proxy 分库分表
