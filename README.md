# 技术方案编写规范&模板

画图工具推荐[Visual Paradigm](https://www.visual-paradigm.com/)(企业版收费，[社区版](https://www.visual-paradigm.com/download/community.jsp?platform=macosx&arch=jre)不收费), [yEd Graph Editor](http://www.yworks.com/products/yed)(免费)，[ProcessOn](https://www.processon.com/)(在线免费)。

## 需求分析

*说明*：根据策划和交互，分析需求，整理需求点，细化需求点。

*目的*：梳理需求，对需求的细节有详细的了解，对大需求做合理拆分，为接下来的详细设计做好充分的需求点准备。

### 需求功能1
* 需求点1
* 需求点1
* ...

### 需求功能2
* 需求点2
* 需求点2
* ...

---

## [XXX]功能设计

*说明*：对单个功能进行分析设计，产出功能的架构图、用例图、时序图、状态图等

### UML用例图
*说明*：使用UML用例图，从用户角度给出用例。如果用例图比较大，可拆成多个用例图。

*举例*：

![o_UML_UseCase_Relationships](https://g.hz.netease.com/cloudmusic/standards/uploads/aa0a6c5b927eb72641da04edf03b7a2f/o_UML_UseCase_Relationships.png)

### 时序图
*说明*：使用时序图，整理出各个系统/模块间的交互

*举例*：

![uml_sequence](https://g.hz.netease.com/cloudmusic/standards/uploads/043ad2c49ba8e11c1bd34b5aadeb244d/uml_sequence.jpg)


### 状态图
*说明*：使用状态图，描述系统中的状态流转

*举例*：

![uml_status](https://g.hz.netease.com/cloudmusic/standards/uploads/0668360e89157d53278caa1ad773cd6f/uml_status.png)

### 流程图
*说明*：使用流程图，梳理出功能的内部逻辑，表示算法基本思路

*举例*：

![查看银行卡信息](https://g.hz.netease.com/cloudmusic/standards/uploads/4b7672f791d738ba51f45c406e1b21da/%E6%9F%A5%E7%9C%8B%E9%93%B6%E8%A1%8C%E5%8D%A1%E4%BF%A1%E6%81%AF.png)

--- 

## 数据设计
*说明*：使用数据表、ER图及数据流转图等工具，展现各领域之间的关系

### 数据表

**【强制】** 数据单表设计需要有表的描述、表名、数据量情况、增量情况以及表字段、主键（如果有）、唯一键、索引。

**【强制】** 多表的情况，设计需要有ER图，表达表之间的关系；并且不同表之间表达同一个含义的字段应该全局统一。

**【强制】** 附上建表语句

**【强制】** 列举词表最常用的sql，印证索引的有效性


*举例*：

#### 权限表
* 表名：Music_PMS_Privilege
* 表类型：单表
* 数据量：10万
* 数据增量：1000条/月
* 索引：idx_app(appCode)
* 建表语句：

``` sql
CREATE TABLE `Music_PMS_Privilege` (
  `id` bigint(20) NOT NULL COMMENT '主键,自增',
  `db_create_time` timestamp NOT NULL DEFAULT '2000-01-01 00:00:00' COMMENT '创建时间',
  `db_update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `updateTime` bigint(20) NOT NULL COMMENT '修改时间',
  `privilegeCode` varchar(256) NOT NULL COMMENT '权限code，唯一键',
  `privilegeName` varchar(256) NOT NULL COMMENT '权限名称，保持唯一',
  `privilegeDesc` varchar(1024) NOT NULL COMMENT '权限描述',
  `privilegeType` bigint(20) NOT NULL COMMENT '权限类型(1:页面权限; 2:功能权限)',
  `appCode` varchar(128) NOT NULL COMMENT '权限隶属的应用名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_privilegeCode` (`privilegeCode`),
  UNIQUE KEY `uk_privilegeName` (`privilegeName`),
  KEY `idx_app` (`appCode`),
  KEY `idx_query` (`privilegeType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PMS权限表' /* BF=privilegeCode, POLICY=music_pms_single, STARTID=1, AUTO_INCREMENT_COLUMN=id, ASSIGNIDTYPE=USB */;
```

* 常用查询：

```sql
select * from Music_PMS_Privilege where appCode = ?
```

| 字段名 | 类型 | 备注 |
| --- | --- | --- |
| id | bigint(20) | 主键，自增 |
| privilegeCode | varchar(256) | 权限code，唯一键 |
| privilegeName | varchar(256) | 权限名称，保持唯一 |
| privilegeDesc | varchar(1024) | 权限描述 |
| privilegeType | int(11) | 权限类型(1:页面权限; 2:功能权限) |
| appCode | varchar(128) | 权限隶属的应用名称 |


#### 用户表
* 表名：Music_PMS_Users
* 表类型：单表
* 数据量：10000
* 数据增量：10条/月
* 索引：idx_name(userName)

| 字段名 | 类型 | 备注 |
| --- | --- | --- |
| id | bigint(20) | 主键，自增 |
| userName | varchar(128) | 用户名称，唯一键 |
| nickName | varchar(256) | 姓名 |
| hzNumber | varchar(128) | 员工号 |


#### ER图
![数据关系图](https://g.hz.netease.com/cloudmusic/standards/uploads/ed05666479814e44a58a4f02cced5f79/%E6%95%B0%E6%8D%AE%E5%BA%93%E5%85%B3%E7%B3%BB%E8%A1%A8.png)

### 数据流转图
*说明*：使用数据流转图，整理出各个模块间的数据流转

![data_flow](https://g.hz.netease.com/cloudmusic/standards/uploads/e7fca7d87897c7514c8e83b8c70fb74d/data_flow.png)

---

## NEI接口设计
**【强制】** 接口设计必须和交互搞呼应，以交互页面为维度编写

**【强制】** 接口需要在NEI中进行维护，并且标注接口等级：[P级定义](https://g.hz.netease.com/cloudmusic/standards/blob/master/coding/api.md)

**【强制】** 标注接口超时时间预计

**【强制】** 标注QPS的预估量级

**【推荐】** 接口设计可以简单标注实现对策（修改的类、修改的表、实现逻辑等）

*举例*：
#### 云豆特权页面
* 交互页面：[云豆特权页面](http://nanny.moonforest.org/jiaohu/web/%E9%9F%B3%E4%B9%90%E4%BA%BA%E6%94%B9%E7%89%88/%E9%9F%B3%E4%B9%90%E4%BA%BA2.0/#g=1&p=云豆特权)

##### 我的云豆
* [NEI接口](https://nei.netease.com/interface/detail/?pid=22594&id=55746)
* 超时时间：100ms，小于5s，无需batch特殊配置
* 预估QPS：100以内
* 技术实现：使用艺人ID查询表Musician_CloudBean

##### 特权列表
* [NEI接口](https://nei.netease.com/interface/detail/res/?pid=22594&id=55748)
* 超时时间：100ms，小于5s，无需batch特殊配置
* 预估QPS：100以内
* 技术实现：
    1. 根据艺人ID和特权code查询表Musician_Artist_SpecialRight，组装可用次数、已用次数；
    2. 根据特权code获取消耗积分的计算方式，计算出一次兑换需要的积分；
    3. 根据艺人ID和特权code和status查询表Musician_Artist_SpecialRight_Record，获取是否有正在审核中的特权记录
* 交互逻辑：前端循环已经开放的每一个特权，调用API接口，获取返回值，展示到页面上 @项方念
* 备注：特权的按钮状态和审核逻辑由前端根据返回值进行判断

---

## RPC接口设计
**【强制】** RPC接口描述（标题）

**【强制】** 使用此接口需要依赖的pom

**【强制】** 接口、方法、入参、出参

**【强制】** 异常列表

**【强制】** RPC提供的API层代码中必须有完善的javadoc规范，规范参考：[RPC JavaDoc 规范]()


*举例*：
#### 获取单个艺人的云豆信息RPC接口
* pom依赖：

```xml
    <dependency>
        <groupId>com.netease.music</groupId>
        <artifactId>musician-cloudbean-api</artifactId>
        <version>1.0</version>
    </dependency>
```

* 接口：com.netease.music.musician.cloudbean.service.CloudBeanRpcService
* 方法：getCloudBeanInfo(Long artistId)
* 入参：artistId(艺人ID)
* 出参：com.netease.music.musician.cloudbean.dto.CloudBeanInfoDto(云豆信息)
* 异常列表：

| 异常码 | 异常描述 | 推荐处理  |
| --- | --- | --- |
| PARAM_ERROR | 参数异常 | 报警并开发人工介入 |
| SPECIAL_RIGHT_RECORDS_COUNT_ERROR | 查询艺人特权记录数量失败 | 客户端重试 |
| | | |

---

## 异步消息设计
**【强制】** 消息描述（标题）

**【推荐】** 消息使用场景介绍

**【强制】** 使用的消息中间件，如无特殊情况使用卡夫卡

**【强制】** 需要依赖的pom

**【强制】** 消息的topic、key以及message类型

**【强制】** 消息的解析方式需要在服务端进行提供，并列出解析方法

*举例*：

#### 云豆特权测试消息
* 使用场景：此消息发送了云豆特权的测试数据
* 消息中间件：kafka
* 依赖pom：

```xml
    <dependency>
        <groupId>com.netease.music</groupId>
        <artifactId>musician-cloudbean-api</artifactId>
        <version>1.0</version>
    </dependency>
```

* topic：musician_cloudbean_topic
* key：test
* 消息体类型：com.netease.music.musician.cloudbean.dto.CloudBeanInfoDto
* 消息体解析方式：CloudBeanInfoDto cloudBeanInfoDto = CloudbeanMessageReaderUtil.readCloudBeanInfo(message);

---

## 风险控制
*说明*：梳理系统的风险，提出需要采取的措施。

### 系统监控
*说明*：描述需要的系统级别的监控点。

| 监控点 | 措施 |
| --- | --- |
| 监控点1 | 措施1 |

### 业务监控
*说明*：描述需要的业务级别的监控点。

| 监控点 | 措施 |
| --- | --- |
| 监控点1 | 措施1 |

### 降级&切换设计
*说明*：设计功能的降级和切换策略

**【强制】** 降级&切换场景描述

**【强制】** 降级&切换使用的工具：[配置中心](http://config.netease.com/#/app)、[Setting](http://music.hz.netease.com/backend/authorize/setting?code=200)

**【强制】** 降级&切换配置项

**【强制】** 降级&切换配置项值含义

*举例*：
#### 歌单页的视频导流降级方案
* 降级场景：用户或者运营反馈、发现线上问题、性能问题
* 降级工具：Setting
* 配置项：mock_related_setting
* 配置项值及含义：

```json
{
   "getFromMockData":"false", //总开关，关闭所有导流推荐
   "demoteToRetuenEmptyForMaxSongSize":300, //歌单页最大歌曲数，小于这个数值的歌单进行导流视频拉取
   "demoteToReturnEmptyListPercent":"0", //歌单页关闭百分比
   "demoteArtistRcmdPercent":"100", //艺人页导流关闭百分比
   "demoteToReturnEmptyListPercentForComment":0, //歌曲评论页导流关闭百分比
   "cacheTime":3600000 //相关视频缓存有效时间
}
```

## 开发计划
*说明*：把设计文档和任务jira关联，记录开发人员和完成时间

**【强制】** 如本需求的开发依赖其它模块，则需要列举依赖模块的开发计划

* 任务地址：

| 模块 | 涉及应用 | 研发人员分工 | 人日 | 截止时间 |
| --- | --- | --- | --- | --- | --- |
| 功能模块1 | 应用1，应用2，... | 研发1，研发2，... | 2人日 | 年月日 |


