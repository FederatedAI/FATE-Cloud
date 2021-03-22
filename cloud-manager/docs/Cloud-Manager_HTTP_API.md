

# Cloud-Manager接口文档


## 1. 版本历史
| 版本状态 | 创建人|  完成日期 | 备注  |
| :--------: | :--------:| :--------:| :--: |
|0.1 版本 |v_wbymxu|2021-03-11 |首次发布|



## 2. 协议概述
本接口支持http协议，请求参数均为JSON格式，当方法为HTTP POST时，必须将Content-type设置为：application/json。所有参数统一使用utf-8编码，以解决可能存在的中文问题。

## 3. 签名验证
Cloud-Manager使用签名方法验证请求用户身份，防止内容被篡改，以及防止重放攻击。接口鉴权主要是验证调用是否有权限调用指定的api服务。
请求时，需要在Header中携带以下参数:

| 参数| 	参数类型	| 描述| 	必填|
| :-------- | :--------| :----------| :-- |
| FATE_MANAGER_USER_ID| 	String| 	机构institutions唯一id| 	是|
| FATE_MANAGER_APP_KEY| 	String| 	机构institutions密钥key| 	是|
| APP_KEY| String| 站点site密钥key，站点维度的接口调用需携带，机构维度的接口调用无需携带| 否|
| PARTY_ID| String | 站点site的parytId，站点维度的接口调用需携带，机构维度的接口调用无需携带| 否|
| ROLE| String | 站点site的角色，站点维度的接口调用需携带，机构维度的接口调用无需携带| 否|
|NONCE  |String|随机串，由调用方生成，比如uuid|是|
| TIMESTAMP	| String| 时间戳，13位精确到毫秒	|是|
| SIGNATURE| String | 签名字符串| 	是|

- 签名计算   

	```java
	#签名分两种情况，机构维度和站点维度
	    
	#机构维度的接口签名
	String signature = generateSignature(FATE_MANAGER_USER_SECRET, FATE_MANAGER_USER_ID, FATE_MANAGER_APP_KEY, TIMESTAMP, NONCE, HTTP_URI, HTTP_BODY);
	
	#站点维度的接口签名
	String signature = generateSignature(FATE_MANAGER_USER_SECRET + APP_SECRET, FATE_MANAGER_USER_ID, FATE_MANAGER_APP_KEY, PARTY_ID,ROLE,APP_KEY,TIMESTAMP, NONCE, HTTP_URI, HTTP_BODY);
	
	#签名方法
	public static String generateSignature(String appSecret, String... encryptParams) throws  Exception {
	            StringBuilder encryptText = new StringBuilder();
	            for (int i = 0; i < encryptParams.length; i++) {
	                if (i != 0) {
	                    encryptText.append("\n");
	                }
	                encryptText.append(encryptParams[i]);
	            }
	            encryptText = new StringBuilder(new String(encryptText.toString().getBytes(), "UTF-8"));
	            byte[] data = appSecret.getBytes("UTF-8");
	            SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
	            Mac mac = Mac.getInstance("HmacSHA1");
	            mac.init(secretKey);
	            byte[] text = encryptText.toString().getBytes("UTF-8");
	            byte[] bytes = mac.doFinal(text);
	            return Base64.getEncoder().encodeToString(bytes);
	    } 
	```
	
- 签名参数说明:

| 字段      |    描述 |
| :-------- | :--------|
|FATE_MANAGER_USER_SECRET|机构institutions的密钥secret|
|APP_SECRET| 站点site的密钥sercret                               |
|FATE_MANAGER_USER_ID| 机构institutions的唯一id                            |
|FATE_MANAGER_APP_KEY| 机构institutions的密钥key                           |
|PARTY_ID|站点site的partyId|
|ROLE|站点site的角色|
|APP_KEY|站点site密钥key|
|TIMESTAMP|表示此次操作的时间，13位精确到毫秒|
|NONCE|表示随机串，由调用方生成，比如 uuid|
|HTTP_URI|表示HTTPS请求URI部分，即从/虚拟跟路径开发部分|
|HTTP_BODY|表示请求HTTP请求的body部分|
|\n| 表示换行符|
## 4. 接口说明
### 4.1 授权模块
#### 4.1.1 查询可申请机构列表
- 请求类型
    post
    
- 请求uri
    /cloud-manager/authority/institutions

- 签名维度
  
  机构
      

- 请求体说明
| 字段      |    类型 | 必填  |描述|
| :-------- | :--------| :--: |:-------- |
|institutions|String|Y|本机构名称|
|pageNum|int|Y|当前页码|
|pageSize|int|Y|分页大小|

- 请求示例
```json
{
  "institutions": "String",
  "pageNum": 1,
  "pageSize": 10
}
```

- 响应体说明
|参数名|类型|注释|
| :-------- | :-------- |:-------- |
|code|int|响应码，详见下面解释|
|message|String|响应信息，详见下面解释|
|data|Object|响应数据对象|
|start|long|开始页|
|end|long|结束页|
|pageNum|long|当前页码|
|pageSize|long|分页大小|
|totalPage|long|总页数|
|totalRecord|long|总记录数|
|startIndex|long|当前页索引|
|institutions|String|机构名称|
|status|int|机构申请状态，1:申请中, 2:同意, 3:拒绝, 4:取消|
- 响应示例
```json
{
  "code": 0,
  "data": {
    "end": 10,
    "list": [
      {
        "institutions": "String",
        "status": 1
      }
    ],
    "pageNum": 1,
    "pageSize": 10,
    "start": 1,
    "startIndex": 0,
    "totalPage": 1,
    "totalRecord": 10
  },
  "msg": "Success!"
}
```

#### 4.1.2 查询机构申请结果
- 请求类型
    post
    
- 请求uri
    /cloud-manager/authority/institutions/approved

- 签名维度

    机构
        

- 请求体说明
| 字段      |    类型 | 必填  |描述|
| :-------- | :--------| :--: |:-------- |
|institutions|String|Y|本机构名称|
|pageNum|int|Y|当前页码|
|pageSize|int|Y|分页大小|

- 请求示例
```json
{
  "institutions": "String",
  "pageNum": 1,
  "pageSize": 10
}
```

- 响应体说明
|参数名|类型|注释|
| :-------- | :-------- |:-------- |
|code|int|响应码，详见下面解释|
|message|String|响应信息，详见下面解释|
|data|Object|响应数据对象|
|start|long|开始页|
|end|long|结束页|
|pageNum|long|当前页码|
|pageSize|long|分页大小|
|totalPage|long|总页数|
|totalRecord|long|总记录数|
|startIndex|long|当前页索引|
|institutions|String|机构名称|
|status|int|机构申请状态，1:申请中, 2:同意, 3:拒绝, 4:取消|
- 响应示例
```json
{
  "code": 0,
  "data": {
    "end": 10,
    "list": [
      {
        "institutions": "String",
        "status": 1
      }
    ],
    "pageNum": 1,
    "pageSize": 10,
    "start": 1,
    "startIndex": 0,
    "totalPage": 1,
    "totalRecord": 10
  },
  "msg": "Success!"
}
```


#### 4.1.3 批量申请机构授权
- 请求类型
    post
    
- 请求uri
    /cloud-manager/authority/institutions/apply

- 签名维度
  
  机构
      

- 请求体说明
| 字段      |    类型 | 必填  |描述|
| :-------- | :--------| :--: |:-------- |
|institutions|String|Y|本机构名称|
|authorityInstitutions|String[]|Y|要申请的机构|


- 请求示例
```json
{
  "authorityInstitutions": [
    "String"
  ],
  "institutions": "String"
}
```

- 响应体说明
|参数名|类型|注释|
| :-------- | :-------- |:-------- |
|code|int|响应码，详见下面解释|
|message|String|响应信息，详见下面解释|
|data|Object|响应数据对象|

- 响应示例
```json
{
  "code": 0,
  "data": {},
  "msg": "Success!"
}
```



#### 4.1.4 查询成功获得授权的机构

- 请求类型
  post
  
- 请求uri
  /cloud-manager/authority/institutions/applied

- 签名维度
  
  机构

- 请求体说明

| 字段         | 类型   | 必填 | 描述       |
| :----------- | :----- | :--: | :--------- |
| institutions | String |  Y   | 本机构名称 |


- 请求示例

```json
{
  "institutions": "String"
}
```

- 响应体说明

| 参数名  | 类型     | 注释                   |
| :------ | :------- | :--------------------- |
| code    | int      | 响应码，详见下面解释   |
| message | String   | 响应信息，详见下面解释 |
| data    | String[] | 获得授权的机构列表     |

- 响应示例

```json
{
  "code": 0,
  "data": [
    "String"
  ],
  "msg": "Success!"
}
```



#### 4.1.5 校核partyId的授权

- 请求类型
  post

- 请求uri
  /cloud-manager/authority/institutions/check/partyId

- 签名维度

  机构

- 请求体说明

| 字段         | 类型   | 必填 | 描述            |
| :----------- | :----- | :--: | :-------------- |
| institutions | String |  Y   | 本机构名称      |
| partyId      | long   |  Y   | 要校核的partyId |




- 请求示例

```json
{
  "institutions": "String",
  "partyId": 1
}
```

- 响应体说明

| 参数名  | 类型    | 注释                   |
| :------ | :------ | :--------------------- |
| code    | int     | 响应码，详见下面解释   |
| message | String  | 响应信息，详见下面解释 |
| data    | boolean | 校核结果，成功或失败   |

- 响应示例

```json
{
  "code": 0,
  "data": true,
  "msg": "Success!"
}
```



### 4.2 站点模块

#### 4.2.1 校核站点激活链接

- 请求类型
  post

- 请求uri
  /cloud-manager/api/site/checkUrl

- 签名维度

  站点

- 请求体说明

| 字段             | 类型   | 必填 | 描述         |
| :--------------- | :----- | :--: | :----------- |
| registrationLink | String |  Y   | 站点激活链接 |


- 请求示例

```json
{
  "registrationLink": "String"
}
```

- 响应体说明

| 参数名       | 类型   | 注释                                           |
| :----------- | :----- | :--------------------------------------------- |
| code         | int    | 响应码，详见下面解释                          |
| message      | String | 响应信息，详见下面解释                         |
| data         | Object | 响应数据对象                                 |

- 响应示例

```json
{
  "code": 0,
  "data": {
  },
  "msg": "Success!"
}
```



#### 4.2.2 站点激活

- 请求类型
  post

- 请求uri
  /cloud-manager/api/site/activate

- 签名维度

  站点

- 请求体说明

| 字段             | 类型   | 必填 | 描述         |
| :--------------- | :----- | :--: | :----------- |
| registrationLink | String |  Y   | 站点激活链接 |


- 请求示例

```json
{
  "registrationLink": "String"
}
```

- 响应体说明

| 参数名  | 类型   | 注释                   |
| :------ | :----- | :--------------------- |
| code    | int    | 响应码，详见下面解释   |
| message | String | 响应信息，详见下面解释 |
| data    | Object | 响应数据对象           |

- 响应示例

```json
{
  "code": 0,
  "data": {
  },
  "msg": "Success!"
}
```



#### 4.2.3 查询站点信息

- 请求类型
  post

- 请求uri
  /cloud-manager/api/site/findOneSite/fateManager

- 签名维度

  站点

- 请求体说明

  无

- 响应体说明

| 参数名  | 类型   | 注释                   |
| :------ | :----- | :--------------------- |
| code    | int    | 响应码，详见下面解释   |
| message | String | 响应信息，详见下面解释 |
| data    | Object | 响应数据对象           |
| activationTime    | long | 激活时间           |
| componentVersion    | String | fate子服务版本        |
| createTime    | long | 创建时间           |
| detectiveStatus    | int | 探测状态           |
| fateServingVersion    | String | fate serving版本           |
| fateVersion    | String | fate版本           |
| groupId    | long | group主键           |
| groupName    | String | group名称           |
| id    | long | 站点主键           |
| institutions    | String | 机构名称           |
| networkAccessEntrances    | String | 网络入口           |
| networkAccessExits    | String | 网络出口           |
| partyId    | long | party id           |
| registrationLink    | String | 注册链接           |
| role    | int | 角色 1:guest, 2:host           |
| secretInfo    | Object | 密钥信息，包括key和secret           |
| siteName    | String | 站点名称           |
| status    | int | 站点状态： 1 未加入, 2 加入, 3 删除         |
| updateTime    | long | 更新时间           |

- 响应示例

```json
{
  "code": 0,
  "data": {
    "activationTime": 0,
    "componentVersion": "String",
    "createTime": 0,
    "detectiveStatus": 0,
    "fateServingVersion": "String",
    "fateVersion": "String",
    "groupId": 0,
    "groupName": "String",
    "id": 0,
    "institutions": "String",
    "networkAccessEntrances": "String",
    "networkAccessExits": "String",
    "partyId": 0,
    "registrationLink": "String",
    "role": 0,
    "secretInfo": {
      "key": "String",
      "secret": "String"
    },
    "siteName": "String",
    "status": 0,
    "updateTime": 0
  },
  "msg": "Success!"
}
```



#### 4.2.3 站点校核

- 请求类型
  post

- 请求uri
  /cloud-manager/api/site/checkAuthority/fateManager

- 签名维度

  站点

- 请求体说明

  直接传String字符串


- 请求示例

```json
 "String"
```

- 响应体说明

| 参数名  | 类型   | 注释                   |
| :------ | :----- | :--------------------- |
| code    | int    | 响应码，详见下面解释   |
| message | String | 响应信息，详见下面解释 |
| data    | Object | 响应数据对象           |

- 响应示例

```json
{
  "code": 0,
  "data": {
  },
  "msg": "Success!"
}
```



#### 4.2.4 IP修改申请

- 请求类型
  post

- 请求uri
  /cloud-manager/api/site/ip/accept

- 签名维度
  站点

- 请求体说明

| 字段                   | 类型   | 必填 | 描述        |
| :--------------------- | :----- | :--: | :---------- |
| networkAccessEntrances | String |  Y   | 网络入口    |
| networkAccessExits     | String |  Y   | 网络出口    |
| partyId                | long   |  Y   | 站点partyId |

- 请求示例

```json
{
  "networkAccessEntrances": "String",
  "networkAccessExits": "String",
  "partyId": 0
}
```

- 响应体说明

| 参数名  | 类型   | 注释                   |
| :------ | :----- | :--------------------- |
| code    | int    | 响应码，详见下面解释   |
| message | String | 响应信息，详见下面解释 |
| data    | Object | 响应数据对象           |
| caseId  | String | 申请序列号             |

- 响应示例

```json
{
  "code": 0,
  "data": {
    "caseId": "String"
  },
  "msg": "Success!"
}
```



#### 4.2.5 IP查询

- 请求类型
  post

- 请求uri
  /cloud-manager/api/site/ip/query

- 签名维度
  站点

- 请求体说明

| 字段    | 类型   | 必填 | 描述        |
| :------ | :----- | :--: | :---------- |
| caseId  | String |  Y   | 申请序列号  |
| partyId | long   |  Y   | 站点partyId |

- 请求示例

```json
{
  "caseId": "String",
  "partyId": 0
}
```

- 响应体说明

| 参数名  | 类型   | 注释                               |
| :------ | :----- | :--------------------------------- |
| code    | int    | 响应码，详见下面解释               |
| message | String | 响应信息，详见下面解释             |
| data    | Object | 响应数据对象                       |
| status  | int    | 申请状态, 0:待处理, 1:同意, 2:拒绝 |

- 响应示例

```json
{
  "code": 0,
  "data": {
    "status": 0
  },
  "msg": "Success!"
}
```



#### 4.2.6 版本更新

- 请求类型
  post

- 请求uri
  /cloud-manager/api/site/fate/version

- 签名维度
  站点

- 请求体说明

| 字段               | 类型   | 必填 | 描述             |
| :----------------- | :----- | :--: | :--------------- |
| componentVersion   | String |  Y   | fate子服务版本   |
| fateServingVersion | String |  Y   | fate serving版本 |
| fateVersion        | String |  Y   | fate版本         |

- 请求示例

```json
{
  "componentVersion": "String",
  "fateServingVersion": "String",
  "fateVersion": "String"
}
```

- 响应体说明

| 参数名  | 类型   | 注释                   |
| :------ | :----- | :--------------------- |
| code    | int    | 响应码，详见下面解释   |
| message | String | 响应信息，详见下面解释 |
| data    | Object | 响应数据对象           |

- 响应示例

```json
{
  "code": 0,
  "data": {
   },
  "msg": "Success!"
}
```



#### 4.2.7 站点分页查询

- 请求类型
  post

- 请求uri
  /cloud-manager/api/site/page/fateManager

- 签名维度
  机构

- 请求体说明

| 字段         | 类型   | 必填 | 描述     |
| :----------- | :----- | :--: | :------- |
| institutions | String |  Y   | 机构名称 |
| pageNum      | int    |  Y   | 页码     |
| pageSize     | int    |  Y   | 分页大小 |

- 请求示例

```json
{
  "institutions": "String",
  "pageNum": 0,
  "pageSize": 0
}
```

- 响应体说明

| 参数名  | 类型   | 注释                   |
| :------ | :----- | :--------------------- |
| code    | int    | 响应码，详见下面解释   |
| message | String | 响应信息，详见下面解释 |
| data    | Object | 响应数据对象           |
| activationTime    | long | 激活时间           |
| componentVersion    | String | fate子服务版本        |
| createTime    | long | 创建时间           |
| detectiveStatus    | int | 探测状态           |
| fateServingVersion    | String | fate serving版本           |
| fateVersion    | String | fate版本           |
| groupId    | long | group主键           |
| groupName    | String | group名称           |
| id    | long | 站点主键           |
| institutions    | String | 机构名称           |
| networkAccessEntrances    | String | 网络入口           |
| networkAccessExits    | String | 网络出口           |
| partyId    | long | party id           |
| registrationLink    | String | 注册链接           |
| role    | int | 角色 1:guest, 2:host           |
| secretInfo    | Object | 密钥信息，包括key和secret           |
| siteName    | String | 站点名称           |
| status    | int | 站点状态： 1 未加入, 2 加入, 3 删除         |
| updateTime    | long | 更新时间           |
|start|long|开始页|
|end|long|结束页|
|pageNum|long|当前页码|
|pageSize|long|分页大小|
|totalPage|long|总页数|
|totalRecord|long|总记录数|
|startIndex|long|当前页索引|

- 响应示例

```json
{
  "code": 0,
   "data": {
    "end": 0,
    "list": [
      {
        "activationTime": 0,
        "componentVersion": "String",
        "createTime": 0,
        "detectiveStatus": 0,
        "fateServingVersion": "String",
        "fateVersion": "String",
        "groupId": 0,
        "groupName": "String",
        "id": 0,
        "institutions": "String",
        "networkAccessEntrances": "String",
        "networkAccessExits": "String",
        "partyId": 0,
        "registrationLink": "String",
        "role": 0,
        "secretInfo": {
          "key": "String",
          "secret": "String"
        },
        "siteName": "String",
        "status": 0,
        "updateTime": 0
      }
    ],
    "pageNum": 0,
    "pageSize": 0,
    "start": 0,
    "startIndex": 0,
    "totalPage": 0,
    "totalRecord": 0
  },
  "msg": "Success!"
}
```



#### 4.2.8 rollsite校核partyId

- 请求类型
  post

- 请求uri
  /cloud-manager/api/site/rollsite/checkPartyId

- 签名说明

  该接口使用的是旧版签名，与上述签名不一样。请求时，需要在Header中携带以下参数:

  | 参数      | 参数类型 | 描述| 必填 |
  | :-------- | :------- | :-------- | :--- |
  | APP_KEY   | String   | 站点site密钥key | 是   |
  | PARTY_ID  | String   | 站点site的parytId | 是   |
  | ROLE      | String   | 站点site的角色 | 是   |
  | NONCE     | String   | 随机串，由调用方生成，比如uuid| 是   |
  | TIMESTAMP | String   | 时间戳，13位精确到毫秒| 是   |
  | SIGNATURE | String   | 签名字符串| 是   |

  - 签名计算   

    ```java
    #旧版站点接口签名，签名方法与上文一致，但是参数不同
    String signature = generateSignature(APP_SECRET,PARTY_ID,ROLE,APP_KEY,TIMESTAMP, NONCE, HTTP_URI, HTTP_BODY);
    ```
    
  - 签名参数说明
  
  | 字段| 描述|
  | :-------------- | :------------- |
  | APP_SECRET| 站点site的密钥sercret|
  | PARTY_ID| 站点site的partyId|
  | ROLE| 站点site的角色|
  | APP_KEY| 站点site密钥key|
  | TIMESTAMP| 表示此次操作的时间，13位精确到毫秒|
  | NONCE| 表示随机串，由调用方生成，比如 uuid|
  | HTTP_URI| 表示HTTPS请求URI部分，即从/虚拟跟路径开发部分 |
  | HTTP_BODY| 表示请求HTTP请求的body部分|
  
- 请求体说明

  无

- 响应体说明

| 参数名  | 类型    | 注释                             |
| :------ | :------ | :------------------------------- |
| code    | int     | 响应码，详见下面解释             |
| message | String  | 响应信息，详见下面解释           |
| data    | Object  | 响应数据对象                     |
| result  | boolean | 校核结果 true 通过，false 未通过 |

- 响应示例

```json
{
  "code": 0,
  "data": {
      result：true
  },
  "msg": "Success!"
}
```



### 4.3 Exchange模块

#### 4.3.1 查询exchange信息

- 请求类型
  post
- 请求uri
  /cloud-manager/api/exchange/exchange/page/fatemanager
- 签名维度
  机构
- 请求体说明

| 字段         | 类型   | 必填 | 描述     |
| :----------- | :----- | :--: | :------- |
| institutions | String |  Y   | 机构名称 |
| pageNum      | int    |  Y   | 页码     |
| pageSize     | int    |  Y   | 分页大小 |

- 请求示例

```json
{
  "institutions": "String",
  "pageNum": 0,
  "pageSize": 0
}
```

- 响应体说明

| 参数名  | 类型   | 注释                   |
| :------ | :----- | :--------------------- |
| code    | int    | 响应码，详见下面解释   |
| message | String | 响应信息，详见下面解释 |
| data    | Object | 响应数据对象           |
| createTime    | Date | 创建时间               |
| exchangeId    | Object | exchangId主键          |
| exchangeName    | Object | exchnage名称           |
| updateTime    | Date | 更新时间               |
| vip    | String | vip的网络(ip:port)               |
|start|long|开始页|
|end|long|结束页|
|pageNum|long|当前页码|
|pageSize|long|分页大小|
|totalPage|long|总页数|
|totalRecord|long|总记录数|
|startIndex|long|当前页索引|

- 响应示例

```json
{
  "code": 0,
  "data": {
    "end": 0,
    "list": [
      {
        "createTime": "2021-03-11T12:09:14.813Z",
        "exchangeId": 0,
        "exchangeName": "String",
        "updateTime": "2021-03-11T12:09:14.813Z",
        "vip": "String"
      }
    ],
    "pageNum": 0,
    "pageSize": 0,
    "start": 0,
    "startIndex": 0,
    "totalPage": 0,
    "totalRecord": 0
  },
  "msg": "Success!"
}
```





### 4.4 Fate-Manager用户模块

#### 4.4.1 激活机构用户

- 请求类型
  post
- 请求uri
  /cloud-manager/api/fate/user/activate
- 签名维度
  机构
- 请求体说明

| 字段             | 类型   | 必填 | 描述     |
| :--------------- | :----- | :--: | :------- |
| registrationLink | String |  Y   | 机构激活链接 |

- 请求示例

```json
{
  "registrationLink": "String"
}
```

- 响应体说明

| 参数名       | 类型   | 注释                   |
| :----------- | :----- | :--------------------- |
| code         | int    | 响应码，详见下面解释   |
| message      | String | 响应信息，详见下面解释 |
| data         | Object | 响应数据对象           |

- 响应示例

```json
{
  "code": 0,
  "data": {
  },
  "msg": "Success!"
}
```

### 4.5 机构功能模块

#### 4.5.1 查询机构功能

- 请求类型
  post
- 请求uri
  /cloud-manager/api/function/find/all/fateManager
- 签名维度
  机构
- 请求体说明
  无

- 响应体说明

| 参数名       | 类型   | 注释                   |
| :----------- | :----- | :--------------------- |
| code         | int    | 响应码，详见下面解释   |
| message      | String | 响应信息，详见下面解释 |
| data         | Object | 响应数据对象           |
| functionId         | long | 功能主键           |
| functionName         | String | 功能名称           |
| status         | int | 功能状态 1:开启 2:关闭           |
- 响应示例

```json
{
  "code": 0,
  "data": [
    {
      "functionId": 0,
      "functionName": "String",
      "status": 0
    }
  ],
  "msg": "Success!"
}
```


### 4.6  Job监控模块

#### 4.6.1 推送job数据

- 请求类型
  post
- 请求uri
  /cloud-manager/api/job/push
- 签名维度
  机构
- 请求体说明
| 字段             | 类型   | 必填 | 描述     |
| :--------------- | :----- | :--: | :------- |
| jobFailedCount | long |  Y   | 失败任务数 |
| jobFinishDate | long |  Y   | 完成时间 |
| jobRunningCount | long |  Y   | 运行任务数 |
| jobSuccessCount | long |  Y   | 成功任务数 |
| jobWaitingCount | long |  Y   | 等待任务数 |
| siteGuestId | long |  Y   | guest站点id |
| siteHostId | long |  Y   | host站点id |

- 请求示例

```json
[
  {
    "jobFailedCount": 0,
    "jobFinishDate": 0,
    "jobRunningCount": 0,
    "jobSuccessCount": 0,
    "jobWaitingCount": 0,
    "siteGuestId": 0,
    "siteHostId": 0
  }
]
```

- 响应体说明

| 参数名       | 类型   | 注释                   |
| :----------- | :----- | :--------------------- |
| code         | int    | 响应码，详见下面解释   |
| message      | String | 响应信息，详见下面解释 |
| data         | Object | 响应数据对象           |

- 响应示例

```json
{
  "code": 0,
  "data": {
  },
  "msg": "Success!"
}
```


### 4.7  联邦组织模块

#### 4.7.1 查询Cloud-Manager的联邦组织信息

- 请求类型
  get
- 请求uri
  /cloud-manager/api/federation/findOrganization
- 签名维度
  站点
- 请求体说明
  无

- 响应体说明

| 参数名       | 类型   | 注释                   |
| :----------- | :----- | :--------------------- |
| code         | int    | 响应码，详见下面解释   |
| message      | String | 响应信息，详见下面解释 |
| data         | Object | 响应数据对象           |
| federatedOrganizationDto         | Object | 响应数据对象           |
| createTime         | long | 创建时间           |
| institution         | String | 机构名称           |
| name         | String | 组织名称           |
| total         | int | 激活的站点数           |


- 响应示例

```json
{
  "code": 0,
  "data": {
    "federatedOrganizationDto": {
      "createTime": 0,
      "institution": "String",
      "name": "String"
    },
    "total": 0
  },
  "msg": "Success!"
}
```

### 4.8  联邦产品版本管理模块

#### 4.8.1 查询联邦产品版本信息

- 请求类型
  post
- 请求uri
  /cloud-manager/api/product/page/fatemanager
- 签名维度
  机构
- 请求体说明
| 字段             | 类型   | 必填 | 描述     |
| :--------------- | :----- | :--: | :------- |
| pageNum | int |  Y   | 页码 |
| pageSize | int|  Y   | 分页大小 |

- 请求示例

```json
{
  "pageNum": 0,
  "pageSize": 0
}
```

- 响应体说明

| 参数名       | 类型   | 注释                   |
| :----------- | :----- | :--------------------- |
| code         | int    | 响应码，详见下面解释   |
| message      | String | 响应信息，详见下面解释 |
| data         | Object | 响应数据对象           |
| createTime         | Date |     创建时间       |
| updateTime         | Date | 更新时间           |
| federatedComponentVersionDos| Array | 子服务对象列表           |
| imageDownloadUrl         | String | 镜像下载url           |
| imageName         | String | 镜像名称           |
| kubernetesChart         | String | kubernetesChart版本           |
| packageDownloadUrl         | String | 安装包下载url           |
| packageName         | String | 安装包名称           |
| productId         | long | 产品id           |
| productName         | String | 产品名称           |
| productVersion         | String | 产品版本           |
| publicStatus         | int | 公开状态 1:公开,2:不公开           |
| componentId         | long | 组件id           |
| componentName         | String | 组件名称           |
| componentVersion         | String | 组件版本           |
| imageRepository         | String | 镜像仓库           |
| imageTag         | String | 镜像标签           |
|start|long|开始页|
|end|long|结束页|
|pageNum|long|当前页码|
|pageSize|long|分页大小|
|totalPage|long|总页数|
|totalRecord|long|总记录数|
|startIndex|long|当前页索引|

- 响应示例

```json
{
  "code": 0,
  "data": {
    "end": 0,
    "list": [
      {
        "createTime": "2021-03-12T02:19:30.760Z",
        "federatedComponentVersionDos": [
          {
            "componentId": 0,
            "componentName": "String",
            "componentVersion": "String",
            "createTime": "2021-03-12T02:19:30.760Z",
            "imageRepository": "String",
            "imageTag": "String",
            "productId": 0,
            "updateTime": "2021-03-12T02:19:30.760Z"
          }
        ],
        "imageDownloadUrl": "String",
        "imageName": "String",
        "kubernetesChart": "String",
        "packageDownloadUrl": "String",
        "packageName": "String",
        "productId": 0,
        "productName": "String",
        "productVersion": "String",
        "publicStatus": 0,
        "updateTime": "2021-03-12T02:19:30.760Z"
      }
    ],
    "pageNum": 0,
    "pageSize": 0,
    "start": 0,
    "startIndex": 0,
    "totalPage": 0,
    "totalRecord": 0
  },
  "msg": "Success!"
}
```

### 4.9  联邦子服务管理模块

#### 4.9.1 添加子服务信息

- 请求类型
  post
- 请求uri
  /cloud-manager/system/add
- 签名维度
  站点
- 请求体说明
| 字段             | 类型   | 必填 | 描述     |
| :--------------- | :----- | :--: | :------- |
| detectiveStatus | int |  Y   | 服务检测状态 |
| id | long|  Y   | 站点id |
| installItems | String|  Y   | 服务名称 |
| type | int |  int   | 产品类型 FATE 或者FATE-Serving等 |
| updateStatus | int|  Y   | 更新状态 |
| updateTime | Date|  Y   | 更新时间 |
| version | String|  Y   | 版本 |

- 请求示例

```json
[
  {
    "detectiveStatus": 0,
    "id": 0,
    "installItems": "String",
    "type": "String",
    "updateStatus": 0,
    "updateTime": "2021-03-12T02:44:55.054Z",
    "version": "String"
  }
]
```

- 响应体说明

| 参数名       | 类型   | 注释                   |
| :----------- | :----- | :--------------------- |
| code         | int    | 响应码，详见下面解释   |
| message      | String | 响应信息，详见下面解释 |
| data         | Object | 响应数据对象           |


- 响应示例

```json
{
  "code": 0,
  "data": {
  },
  "msg": "Success!"
}
```
#### 4.9.2 子服务心跳

- 请求类型
  post
- 请求uri
  /cloud-manager/system/heart
- 签名维度
  站点
- 请求体说明
| 字段             | 类型   | 必填 | 描述     |
| :--------------- | :----- | :--: | :------- |
| detectiveStatus | int |  Y   | 服务检测状态 |
| id | long|  Y   | 站点id |
| installItems | String|  Y   | 服务名称 |
| version | String|  Y   | 版本 |

- 请求示例

```json
[
  {
    "detectiveStatus": 0,
    "id": 0,
    "installItems": "String",
    "version": "String"
  }
]
```

- 响应体说明

| 参数名       | 类型   | 注释                   |
| :----------- | :----- | :--------------------- |
| code         | int    | 响应码，详见下面解释   |
| message      | String | 响应信息，详见下面解释 |
| data         | Object | 响应数据对象           |


- 响应示例

```json
{
  "code": 0,
  "data": {
  },
  "msg": "Success!"
}
```

## 5. 响应码

|响应码|解释|
| :-------- | :--------|
|0        |请求成功|
|100        |联邦组织已经注册|
|101        |输入参数异常|
|102        |partyId取值范围已经被占用|
|103        |该partyId已经被使用|
|104        |groupSet不存在|
|105        |groupSet 已经被站点引用|
|106        |partyId不存在,或者已经激活|
|107        |partyId已经被修改|
|108        |partyId不在groupSet取值范围内|
|109        |网络访问异常|
|110        |系统错误|
|111        |鉴权失败|
|112        |站点未存活|
|113        |密钥不存在|
|114        |数据不存在|
|115        |partyId不存在|
|116        |partyId已被删除|
|117        |联邦组织未注册|
|118        |新的groupSet取值范围不包含已使用的站点partyId|
|119        |缺少case_id|
|120        |case_id处理失败|
|121        |更新Fate相关版本失败|
|122        |站点名称已存在|
|123        |gorupSet名称已存在|
|124        |gorupSet名称不能为空|
|125        |gorupSet取值范围已被使用|
|126        |机构已经发出过申请|
|127        | 该功能已关闭                                  |
|128        | cloud-manager用户已存在                       |
|129        | 用户密码错误                                  |
|130        | 请先登入                                      |
|131        | cloud-manager最后的用户不能删除               |
|132        | 账号或密码错误                                |
|133        | 鉴权失败                                      |
|134        | 机构已被使用                                  |
|135        |内存溢出|
|136        | 机构不存在                                    |
|137        |rollsite访问异常|
|138        | rollsite已存在                                |
|139        | exchangeName已存在                            |
|140        | rollsite网络已存在                            |
|141        | 该机构不可编辑                                |
|142        |该机构激活链接已更改|
