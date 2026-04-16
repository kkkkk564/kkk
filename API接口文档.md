# 汉文化社交应用 API 接口文档

> 版本：v2.0
>
> 基础URL：`https://api.hanculture.com/api`
>
> 认证方式：JWT Token（Header: `Authorization: Bearer <token>`）



## 通用说明

### 统一响应格式

所有接口均返回以下 JSON 格式：

```json
{
  "code": 0,
  "message": "操作成功",
  "data": {}
}
```

| 字段      | 类型           | 说明             |
| ------- | ------------ | -------------- |
| code    | number       | 响应码，0-成功，非0-失败 |
| message | string       | 提示信息           |
| data    | object/array | 返回的数据          |

### 错误码说明

| code | 说明          |
| ---- | ----------- |
| 0    | 成功          |
| 1001 | 参数错误        |
| 1002 | 用户已存在       |
| 1003 | 用户不存在或密码错误  |
| 1004 | Token无效或已过期 |
| 1005 | 无权限操作       |
| 2001 | 帖子不存在       |
| 2002 | 评论不存在       |

***

## 一、用户模块

### 1.1 用户注册

#### 基本信息

> 请求路径：/user/register
>
> 请求方式：POST
>
> 接口描述：该接口用于新用户注册账号

#### 请求参数

请求参数类型：application/json

请求参数说明：

| 名称       | 类型     | 是否必须 | 默认值    | 备注                       | 其他信息   |
| :------- | :----- | :--- | :----- | :----------------------- | :----- |
| username | string | 必须   | <br /> | 用户名，3-20个字符，支持中英文、数字、下划线 | <br /> |
| password | string | 必须   | <br /> | 密码，6-20个字符               | <br /> |

请求数据样例：

```json
{
  "username": "文化爱好者",
  "password": "123456"
}
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称              | 类型     | 是否必须 | 默认值    | 备注                   | 其他信息   |
| :-------------- | :----- | :--- | :----- | :------------------- | :----- |
| code            | number | 必须   | <br /> | 响应码, 0-成功,1-失败       | <br /> |
| message         | string | 非必须  | <br /> | 提示信息                 | <br /> |
| data            | object | 必须   | <br /> | 返回的数据                | <br /> |
| \|-token        | string | 必须   | <br /> | JWT访问令牌，用于后续接口认证     | <br /> |
| \|-refreshToken | string | 必须   | <br /> | JWT刷新令牌，用于Token过期时刷新 | <br /> |
| \|-user         | object | 必须   | <br /> | 用户信息                 | <br /> |
| \|\|-id         | number | 必须   | <br /> | 用户ID                 | <br /> |
| \|\|-name       | string | 必须   | <br /> | 用户名                  | <br /> |
| \|\|-avatar     | string | 非必须  | <br /> | 头像地址（默认头像）           | <br /> |
| \|\|-bio        | string | 非必须  | <br /> | 个人简介                 | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "name": "文化爱好者",
      "avatar": "https://ui-avatars.com/api/?name=文&background=random",
      "bio": null
    }
  }
}
```

***

### 1.2 用户登录

#### 基本信息

> 请求路径：/user/login
>
> 请求方式：POST
>
> 接口描述：该接口用于用户登录获取认证令牌

#### 请求参数

请求参数类型：application/json

请求参数说明：

| 名称       | 类型     | 是否必须 | 默认值    | 备注  | 其他信息   |
| :------- | :----- | :--- | :----- | :-- | :----- |
| username | string | 必须   | <br /> | 用户名 | <br /> |
| password | string | 必须   | <br /> | 密码  | <br /> |

请求数据样例：

```json
{
  "username": "文化爱好者",
  "password": "123456"
}
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称              | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :-------------- | :----- | :--- | :----- | :------------- | :----- |
| code            | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message         | string | 非必须  | <br /> | 提示信息           | <br /> |
| data            | object | 必须   | <br /> | 返回的数据          | <br /> |
| \|-token        | string | 必须   | <br /> | JWT访问令牌        | <br /> |
| \|-refreshToken | string | 必须   | <br /> | JWT刷新令牌        | <br /> |
| \|-user         | object | 必须   | <br /> | 用户信息           | <br /> |
| \|\|-id         | number | 必须   | <br /> | 用户ID           | <br /> |
| \|\|-name       | string | 必须   | <br /> | 用户名            | <br /> |
| \|\|-avatar     | string | 非必须  | <br /> | 头像地址           | <br /> |
| \|\|-bio        | string | 非必须  | <br /> | 个人简介           | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "name": "文化爱好者",
      "avatar": "https://example.com/avatar.png",
      "bio": "热爱汉文化的传播者"
    }
  }
}
```

***

### 1.3 Token刷新

#### 基本信息

> 请求路径：/user/refresh
>
> 请求方式：POST
>
> 接口描述：该接口用于当访问令牌（Token）过期时，使用刷新令牌获取新的访问令牌
>
> **需要认证**：否（但需要提供有效的refreshToken）

#### 请求参数

请求参数类型：application/json

请求参数说明：

| 名称           | 类型     | 是否必须 | 默认值    | 备注         | 其他信息   |
| :----------- | :----- | :--- | :----- | :--------- | :----- |
| refreshToken | string | 必须   | <br /> | 登录时获取的刷新令牌 | <br /> |

请求数据样例：

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称              | 类型     | 是否必须 | 默认值    | 备注              | 其他信息   |
| :-------------- | :----- | :--- | :----- | :-------------- | :----- |
| code            | number | 必须   | <br /> | 响应码, 0-成功,非0-失败 | <br /> |
| message         | string | 非必须  | <br /> | 提示信息            | <br /> |
| data            | object | 必须   | <br /> | 返回的数据           | <br /> |
| \|-token        | string | 必须   | <br /> | 新的JWT访问令牌       | <br /> |
| \|-refreshToken | string | 必须   | <br /> | 新的刷新令牌（可与旧令牌相同） | <br /> |
| \|-user         | object | 必须   | <br /> | 用户信息（可选）        | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.newtoken...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.newrefreshtoken...",
    "user": {
      "id": 1,
      "name": "文化爱好者",
      "avatar": "https://example.com/avatar.png",
      "bio": "热爱汉文化的传播者"
    }
  }
}
```

#### 错误码

| code | 说明                 |
| ---- | ------------------ |
| 0    | 刷新成功               |
| 1004 | refreshToken无效或已过期 |

#### 实现建议

- 客户端应在收到 401 响应时自动调用此接口
- 刷新成功后自动重试原请求
- 如果刷新失败，应清除本地Token并跳转登录页面
- 建议使用 AtomicBoolean 防止并发刷新

***

### 1.4 获取当前登录用户信息

#### 基本信息

> 请求路径：/user/profile
>
> 请求方式：GET
>
> 接口描述：该接口用于获取当前已登录用户的详细信息
>
> **需要认证**：是（Header: Authorization: Bearer <token>）

#### 请求参数

无

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称        | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :-------- | :----- | :--- | :----- | :------------- | :----- |
| code      | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message   | string | 非必须  | <br /> | 提示信息           | <br /> |
| data      | object | 必须   | <br /> | 返回的数据          | <br /> |
| \|-id     | number | 必须   | <br /> | 用户ID           | <br /> |
| \|-name   | string | 必须   | <br /> | 用户名            | <br /> |
| \|-avatar | string | 非必须  | <br /> | 头像地址           | <br /> |
| \|-bio    | string | 非必须  | <br /> | 个人简介           | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "admin",
    "avatar": "https://example.com/avatar.png",
    "bio": "热爱汉文化的传播者"
  }
}
```

***

### 1.5 更新用户资料

#### 基本信息

> 请求路径：/user/profile
>
> 请求方式：PUT
>
> 接口描述：该接口用于更新用户的个人资料（头像、简介等）
>
> **需要认证**：是

#### 请求参数

请求参数类型：application/json

请求参数说明：

| 名称     | 类型     | 是否必须 | 默认值    | 备注               | 其他信息   |
| :----- | :----- | :--- | :----- | :--------------- | :----- |
| avatar | string | 非必须  | <br /> | 头像地址（通过文件上传接口获取） | <br /> |
| bio    | string | 非必须  | <br /> | 个人简介，最多200字      | <br /> |

请求数据样例：

```json
{
  "avatar": "https://oss.hanculture.com/avatars/user_123.png",
  "bio": "热爱传统文化，传承华夏文明"
}
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称        | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :-------- | :----- | :--- | :----- | :------------- | :----- |
| code      | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message   | string | 非必须  | <br /> | 提示信息           | <br /> |
| data      | object | 必须   | <br /> | 更新后的用户信息       | <br /> |
| \|-id     | number | 必须   | <br /> | 用户ID           | <br /> |
| \|-name   | string | 必须   | <br /> | 用户名            | <br /> |
| \|-avatar | string | 非必须  | <br /> | 新头像地址          | <br /> |
| \|-bio    | string | 非必须  | <br /> | 新个人简介          | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "更新成功",
  "data": {
    "id": 1,
    "name": "文化爱好者",
    "avatar": "https://oss.hanculture.com/avatars/user_123.png",
    "bio": "热爱传统文化，传承华夏文明"
  }
}
```

***

### 1.6 用户登出

#### 基本信息

> 请求路径：/user/logout
>
> 请求方式：POST
>
> 接口描述：该接口用于用户登出，使Token失效
>
> **需要认证**：是

#### 请求参数

无

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称      | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :------ | :----- | :--- | :----- | :------------- | :----- |
| code    | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message | string | 非必须  | <br /> | 提示信息           | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "登出成功"
}
```

***

## 二、帖子模块

### 2.1 获取帖子列表

#### 基本信息

> 请求路径：/posts
>
> 请求方式：GET
>
> 接口描述：该接口用于获取帖子列表，支持分页和分类筛选

#### 请求参数

请求参数类型：Query Params

请求参数说明：

| 名称   | 类型     | 是否必须 | 默认值       | 备注                                                                                    | 其他信息   |
| :--- | :----- | :--- | :-------- | :------------------------------------------------------------------------------------ | :----- |
| type | string | 非必须  | <br />    | 帖子类型筛选，可选值：Hanfu(汉服), Poetry(诗词), Music(音乐), Etiquette(礼仪), Solar(节气), UserPost(用户发布) | <br /> |
| page | number | 非必须  | 0         | 页码，从0开始                                                                               | <br /> |
| size | number | 非必须  | 10        | 每页条数，最大50                                                                             | <br /> |
| sort | string | 非必须  | createdAt | 排序字段，可选：createdAt(最新), likesCount(最热)                                                 | <br /> |

请求示例：

```
GET /posts?type=Hanfu&page=0&size=10&sort=createdAt
GET /posts?page=0&size=20
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称                 | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :----------------- | :----- | :--- | :----- | :------------- | :----- |
| code               | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message            | string | 非必须  | <br /> | 提示信息           | <br /> |
| data               | object | 必须   | <br /> | 返回的数据          | <br /> |
| \|-content         | array  | 非必须  | <br /> | 帖子列表           | <br /> |
| \|\|-id            | number | 非必须  | <br /> | 帖子ID           | <br /> |
| \|\|-title         | string | 非必须  | <br /> | 帖子标题           | <br /> |
| \|\|-description   | string | 非必须  | <br /> | 帖子摘要（最多50字）    | <br /> |
| \|\|-imageUrl      | string | 非必须  | <br /> | 封面图片地址         | <br /> |
| \|\|-type          | string | 非必须  | <br /> | 帖子类型           | <br /> |
| \|\|-authorName    | string | 非必须  | <br /> | 作者名称           | <br /> |
| \|\|-authorAvatar  | string | 非必须  | <br /> | 作者头像           | <br /> |
| \|\|-likesCount    | number | 非必须  | <br /> | 点赞数            | <br /> |
| \|\|-commentsCount | number | 非必须  | <br /> | 评论数            | <br /> |
| \|\|-createdAt     | string | 非必须  | <br /> | 发布时间           | <br /> |
| \|-totalElements   | number | 非必须  | <br /> | 总记录数           | <br /> |
| \|-totalPages      | number | 非必须  | <br /> | 总页数            | <br /> |
| \|-currentPage     | number | 非必须  | <br /> | 当前页码           | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "查询成功",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "齐胸襦裙",
        "description": "汉服经典款式，展现女性柔美身姿。",
        "imageUrl": "https://oss.hanculture.com/posts/img_001.jpg",
        "type": "Hanfu",
        "authorName": "官方小编",
        "authorAvatar": "https://example.com/admin_avatar.png",
        "likesCount": 128,
        "commentsCount": 32,
        "createdAt": "2024-01-20 14:30:00"
      },
      {
        "id": 2,
        "title": "静夜思",
        "description": "李白（唐）- 床前明月光，疑是地上霜。",
        "imageUrl": "https://oss.hanculture.com/posts/img_002.jpg",
        "type": "Poetry",
        "authorName": "官方小编",
        "authorAvatar": "https://example.com/admin_avatar.png",
        "likesCount": 256,
        "commentsCount": 48,
        "createdAt": "2024-01-19 10:15:00"
      }
    ],
    "totalElements": 25,
    "totalPages": 3,
    "currentPage": 0
  }
}
```

***

### 2.2 获取帖子详情

#### 基本信息

> 请求路径：/posts/{id}
>
> 请求方式：GET
>
> 接口描述：该接口用于获取单个帖子的详细信息及评论列表

#### 请求参数

路径参数说明：

| 名称 | 类型     | 是否必须 | 默认值    | 备注   | 其他信息   |
| :- | :----- | :--- | :----- | :--- | :----- |
| id | number | 必须   | <br /> | 帖子ID | path变量 |

请求示例：

```
GET /posts/1
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称                 | 类型      | 是否必须 | 默认值    | 备注                       | 其他信息   |
| :----------------- | :------ | :--- | :----- | :----------------------- | :----- |
| code               | number  | 必须   | <br /> | 响应码, 0-成功,1-失败           | <br /> |
| message            | string  | 非必须  | <br /> | 提示信息                     | <br /> |
| data               | object  | 必须   | <br /> | 返回的数据                    | <br /> |
| \|-post            | object  | 非必须  | <br /> | 帖子详情                     | <br /> |
| \|\|-id            | number  | 非必须  | <br /> | 帖子ID                     | <br /> |
| \|\|-title         | string  | 非必须  | <br /> | 标题                       | <br /> |
| \|\|-description   | string  | 非必须  | <br /> | 摘要                       | <br /> |
| \|\|-content       | string  | 非必须  | <br /> | 正文内容（支持换行符\n）            | <br /> |
| \|\|-imageUrl      | string  | 非必须  | <br /> | 封面图片地址                   | <br /> |
| \|\|-type          | string  | 非必须  | <br /> | 类型                       | <br /> |
| \|\|-authorName    | string  | 非必须  | <br /> | 作者名                      | <br /> |
| \|\|-authorAvatar  | string  | 非必须  | <br /> | 作者头像                     | <br /> |
| \|\|-likesCount    | number  | 非必须  | <br /> | 点赞数                      | <br /> |
| \|\|-commentsCount | number  | 非必须  | <br /> | 评论数                      | <br /> |
| \|\|-isLiked       | boolean | 非必须  | <br /> | 当前用户是否已点赞（未登录时为false）    | <br /> |
| \|\|-createdAt     | string  | 非必须  | <br /> | 发布时间                     | <br /> |
| \|-comments        | array   | 非必须  | <br /> | 评论列表                     | <br /> |
| \|\|-id            | number  | 非必须  | <br /> | 评论ID                     | <br /> |
| \|\|-username      | string  | 非必须  | <br /> | 评论者用户名                   | <br /> |
| \|\|-avatarUrl     | string  | 非必须  | <br /> | 评论者头像                    | <br /> |
| \|\|-content       | string  | 非必须  | <br /> | 评论内容                     | <br /> |
| \|\|-timestamp     | string  | 非必须  | <br /> | 评论时间，格式：yyyy-MM-dd HH:mm | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "查询成功",
  "data": {
    "post": {
      "id": 1,
      "title": "齐胸襦裙",
      "description": "汉服经典款式，展现女性柔美身姿。",
      "content": "齐胸襦裙是对隋唐五代时期特有的一种女子襦裙装的称呼。在古代，一般女子的襦裙上襦是交领的，而齐胸襦裙的上襦多为对襟。",
      "imageUrl": "https://oss.hanculture.com/posts/img_001.jpg",
      "type": "Hanfu",
      "authorName": "官方小编",
      "authorAvatar": "https://example.com/admin_avatar.png",
      "likesCount": 128,
      "commentsCount": 5,
      "isLiked": true,
      "createdAt": "2024-01-20 14:30:00"
    },
    "comments": [
      {
        "id": 1,
        "username": "文化爱好者",
        "avatarUrl": "https://ui-avatars.com/api/?name=文",
        "content": "这篇文章写得太好了，涨知识了！",
        "timestamp": "2024-01-21 10:00"
      },
      {
        "id": 2,
        "username": "汉服同好",
        "avatarUrl": "https://ui-avatars.com/api/?name=汉",
        "content": "请问哪里可以定制这种款式？",
        "timestamp": "2024-01-21 11:30"
      }
    ]
  }
}
```

***

### 2.3 发布帖子

#### 基本信息

> 请求路径：/posts
>
> 请求方式：POST
>
> 接口描述：该接口用于发布新的帖子
>
> **需要认证**：是

#### 请求参数

请求参数类型：application/json

请求参数说明：

| 名称          | 类型     | 是否必须 | 默认值    | 备注                                                        | 其他信息   |
| :---------- | :----- | :--- | :----- | :-------------------------------------------------------- | :----- |
| title       | string | 必须   | <br /> | 帖子标题，2-100字                                               | <br /> |
| description | string | 非必须  | <br /> | 帖子摘要（自动截取content前50字，也可手动指定）                              | <br /> |
| content     | string | 必须   | <br /> | 帖子正文内容                                                    | <br /> |
| imageUrl    | string | 必须   | <br /> | 封面图片地址（需先调用上传接口获取）                                        | <br /> |
| type        | string | 必须   | <br /> | 帖子类型，可选值：Hanfu, Poetry, Music, Etiquette, Solar, UserPost | <br /> |

请求数据样例：

```json
{
  "title": "我的第一件汉服",
  "description": "分享一下我最近入手的汉服...",
  "content": "终于入手了人生中第一件汉服！选的是齐胸襦裙款式，穿上之后感觉整个人都变优雅了。推荐大家也尝试一下传统服饰的魅力。",
  "imageUrl": "https://oss.hanculture.com/uploads/user_post_123.jpg",
  "type": "UserPost"
}
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称           | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :----------- | :----- | :--- | :----- | :------------- | :----- |
| code         | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message      | string | 非必须  | <br /> | 提示信息           | <br /> |
| data         | object | 必须   | <br /> | 创建成功的帖子信息      | <br /> |
| \|-id        | number | 非必须  | <br /> | 帖子ID           | <br /> |
| \|-title     | string | 非必须  | <br /> | 帖子标题           | <br /> |
| \|-imageUrl  | string | 非必须  | <br /> | 图片地址           | <br /> |
| \|-type      | string | 非必须  | <br /> | 类型             | <br /> |
| \|-createdAt | string | 非必须  | <br /> | 创建时间           | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "发布成功",
  "data": {
    "id": 101,
    "title": "我的第一件汉服",
    "imageUrl": "https://oss.hanculture.com/uploads/user_post_123.jpg",
    "type": "UserPost",
    "createdAt": "2024-01-22 16:45:00"
  }
}
```

***

### 2.4 删除帖子

#### 基本信息

> 请求路径：/posts/{id}
>
> 请求方式：DELETE
>
> 接口描述：该接口用于删除指定帖子（仅限作者本人）
>
> **需要认证**：是

#### 请求参数

路径参数说明：

| 名称 | 类型     | 是否必须 | 默认值    | 备注   | 其他信息   |
| :- | :----- | :--- | :----- | :--- | :----- |
| id | number | 必须   | <br /> | 帖子ID | path变量 |

请求示例：

```
DELETE /posts/101
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称      | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :------ | :----- | :--- | :----- | :------------- | :----- |
| code    | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message | string | 非必须  | <br /> | 提示信息           | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "删除成功"
}
```

***

### 2.5 获取我的帖子

#### 基本信息

> 请求路径：/posts/my
>
> 请求方式：GET
>
> 接口描述：该接口用于获取当前登录用户发布的所有帖子
>
> **需要认证**：是

#### 请求参数

Query 参数说明：

| 名称   | 类型     | 是否必须 | 默认值 | 备注      | 其他信息   |
| :--- | :----- | :--- | :-- | :------ | :----- |
| page | number | 非必须  | 0   | 页码，从0开始 | <br /> |
| size | number | 非必须  | 10  | 每页条数    | <br /> |

请求示例：

```
GET /posts/my?page=0&size=10
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

与 [2.1 获取帖子列表](#21-获取帖子列表) 相同

响应数据样例：

```json
{
  "code": 0,
  "message": "查询成功",
  "data": {
    "content": [
      {
        "id": 101,
        "title": "我的第一件汉服",
        "description": "分享一下我最近入手的汉服...",
        "imageUrl": "https://oss.hanculture.com/uploads/user_post_123.jpg",
        "type": "UserPost",
        "authorName": "文化爱好者",
        "authorAvatar": "https://ui-avatars.com/api/?name=文",
        "likesCount": 12,
        "commentsCount": 3,
        "createdAt": "2024-01-22 16:45:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0
  }
}
```

***

## 三、评论模块

### 3.1 发表评论

#### 基本信息

> 请求路径：/comments
>
> 请求方式：POST
>
> 接口描述：该接口用于对指定帖子发表评论
>
> **需要认证**：是

#### 请求参数

请求参数类型：application/json

请求参数说明：

| 名称      | 类型     | 是否必须 | 默认值    | 备注          | 其他信息   |
| :------ | :----- | :--- | :----- | :---------- | :----- |
| postId  | number | 必须   | <br /> | 目标帖子ID      | <br /> |
| content | string | 必须   | <br /> | 评论内容，1-500字 | <br /> |

请求数据样例：

```json
{
  "postId": 1,
  "content": "这篇文章写得太好了，涨知识了！"
}
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称           | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :----------- | :----- | :--- | :----- | :------------- | :----- |
| code         | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message      | string | 非必须  | <br /> | 提示信息           | <br /> |
| data         | object | 必须   | <br /> | 创建成功的评论信息      | <br /> |
| \|-id        | number | 非必须  | <br /> | 评论ID           | <br /> |
| \|-username  | string | 非必须  | <br /> | 评论者用户名         | <br /> |
| \|-avatarUrl | string | 非必须  | <br /> | 评论者头像          | <br /> |
| \|-content   | string | 非必须  | <br /> | 评论内容           | <br /> |
| \|-timestamp | string | 非必须  | <br /> | 评论时间           | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "评论发表成功",
  "data": {
    "id": 10,
    "username": "文化爱好者",
    "avatarUrl": "https://ui-avatars.com/api/?name=文",
    "content": "这篇文章写得太好了，涨知识了！",
    "timestamp": "2024-01-22 17:00:00"
  }
}
```

***

### 3.2 删除评论

#### 基本信息

> 请求路径：/comments/{id}
>
> 请求方式：DELETE
>
> 接口描述：该接口用于删除指定评论（仅限评论者本人）
>
> **需要认证**：是

#### 请求参数

路径参数说明：

| 名称 | 类型     | 是否必须 | 默认值    | 备注   | 其他信息   |
| :- | :----- | :--- | :----- | :--- | :----- |
| id | number | 必须   | <br /> | 评论ID | path变量 |

请求示例：

```
DELETE /comments/10
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称      | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :------ | :----- | :--- | :----- | :------------- | :----- |
| code    | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message | string | 非必须  | <br /> | 提示信息           | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "删除成功"
}
```

***

## 四、点赞模块

### 4.1 点赞帖子

#### 基本信息

> 请求路径：/likes/{postId}
>
> 请求方式：POST
>
> 接口描述：该接口用于对指定帖子进行点赞（重复点赞会返回提示）
>
> **需要认证**：是

#### 请求参数

路径参数说明：

| 名称     | 类型     | 是否必须 | 默认值    | 备注     | 其他信息   |
| :----- | :----- | :--- | :----- | :----- | :----- |
| postId | number | 必须   | <br /> | 目标帖子ID | path变量 |

请求示例：

```
POST /likes/1
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称            | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :------------ | :----- | :--- | :----- | :------------- | :----- |
| code          | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message       | string | 非必须  | <br /> | 提示信息           | <br /> |
| data          | object | 非必须  | <br /> | 返回的数据          | <br /> |
| \|-likesCount | number | 非必须  | <br /> | 点赞后的总点赞数       | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "点赞成功",
  "data": {
    "likesCount": 129
  }
}
```

***

### 4.2 取消点赞

#### 基本信息

> 请求路径：/likes/{postId}
>
> 请求方式：DELETE
>
> 接口描述：该接口用于取消对指定帖子的点赞
>
> **需要认证**：是

#### 请求参数

路径参数说明：

| 名称     | 类型     | 是否必须 | 默认值    | 备注     | 其他信息   |
| :----- | :----- | :--- | :----- | :----- | :----- |
| postId | number | 必须   | <br /> | 目标帖子ID | path变量 |

请求示例：

```
DELETE /likes/1
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称            | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :------------ | :----- | :--- | :----- | :------------- | :----- |
| code          | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message       | string | 非必须  | <br /> | 提示信息           | <br /> |
| data          | object | 非必须  | <br /> | 返回的数据          | <br /> |
| \|-likesCount | number | 非必须  | <br /> | 取消后的总点赞数       | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "取消成功",
  "data": {
    "likesCount": 128
  }
}
```

***

### 4.3 检查点赞状态

#### 基本信息

> 请求路径：/likes/check/{postId}
>
> 请求方式：GET
>
> 接口描述：该接口用于检查当前用户是否已对指定帖子点赞
>
> **需要认证**：是（未登录时返回 isLiked: false）

#### 请求参数

路径参数说明：

| 名称     | 类型     | 是否必须 | 默认值    | 备注     | 其他信息   |
| :----- | :----- | :--- | :----- | :----- | :----- |
| postId | number | 必须   | <br /> | 目标帖子ID | path变量 |

请求示例：

```
GET /likes/check/1
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称            | 类型      | 是否必须 | 默认值    | 备注             | 其他信息   |
| :------------ | :------ | :--- | :----- | :------------- | :----- |
| code          | number  | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message       | string  | 非必须  | <br /> | 提示信息           | <br /> |
| data          | object  | 必须   | <br /> | 返回的数据          | <br /> |
| \|-isLiked    | boolean | 非必须  | <br /> | 是否已点赞          | <br /> |
| \|-likesCount | number  | 非必须  | <br /> | 总点赞数           | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "查询成功",
  "data": {
    "isLiked": true,
    "likesCount": 128
  }
}
```

***

## 五、文件上传模块（阿里云OSS）

### 5.1 上传图片

#### 基本信息

> 请求路径：/upload/image
>
> 请求方式：POST
>
> 接口描述：该接口用于上传图片到阿里云OSS，返回可访问的图片URL
>
> **需要认证**：是
>
> Content-Type：multipart/form-data

#### 请求参数

请求参数类型：multipart/form-data

请求参数说明：

| 名称   | 类型     | 是否必须 | 默认值    | 备注                                         | 其他信息   |
| :--- | :----- | :--- | :----- | :----------------------------------------- | :----- |
| file | file   | 必须   | <br /> | 图片文件，支持 jpg/png/gif/webp 格式，大小不超过 10MB     | <br /> |
| type | string | 非必须  | post   | 上传用途，可选值：avatar(头像), post(帖子封面), other(其他) | <br /> |

请求示例（使用 Postman 或代码）：

```
POST /upload/image
Content-Type: multipart/form-data

file: [二进制图片文件]
type: post
```

#### 响应数据

响应数据类型：application/json

响应参数说明：

| 名称          | 类型     | 是否必须 | 默认值    | 备注             | 其他信息   |
| :---------- | :----- | :--- | :----- | :------------- | :----- |
| code        | number | 必须   | <br /> | 响应码, 0-成功,1-失败 | <br /> |
| message     | string | 非必须  | <br /> | 提示信息           | <br /> |
| data        | object | 必须   | <br /> | 返回的数据          | <br /> |
| \|-url      | string | 非必须  | <br /> | 上传后的图片访问地址     | <br /> |
| \|-fileName | string | 非必须  | <br /> | 文件名            | <br /> |
| \|-fileSize | number | 非必须  | <br /> | 文件大小（字节）       | <br /> |
| \|-width    | number | 非必须  | <br /> | 图片宽度（像素）       | <br /> |
| \|-height   | number | 非必须  | <br /> | 图片高度（像素）       | <br /> |

响应数据样例：

```json
{
  "code": 0,
  "message": "上传成功",
  "data": {
    "url": "https://oss.hanculture.com/uploads/post_20240122_174500_abc123.jpg",
    "fileName": "post_20240122_174500_abc123.jpg",
    "fileSize": 245760,
    "width": 1200,
    "height": 800
  }
}
```

***

## 六、附录

### A. 帖子类型枚举说明

| 类型值       | 中文名称 | 说明           |
| --------- | ---- | ------------ |
| Hanfu     | 汉服   | 关于汉服款式、穿搭等内容 |
| Poetry    | 诗词   | 古诗词赏析、创作等    |
| Music     | 音乐   | 传统音乐、乐器介绍等   |
| Etiquette | 礼仪   | 传统礼仪、习俗等     |
| Solar     | 节气   | 二十四节气相关内容    |
| UserPost  | 用户发布 | 用户原创内容       |

### B. 错误处理建议

客户端应根据以下情况做相应处理：

1. **网络错误**：显示"网络连接失败，请检查网络后重试"
2. **Token过期(code=1004)**：自动调用刷新接口，如刷新失败则清除本地Token，跳转登录页面
3. **权限不足(code=1005)**：显示"您没有权限执行此操作"
4. **资源不存在(code=2001/2002)**：显示"内容已被删除或不存在"

### B.1 Token刷新机制

#### 客户端实现建议

```
┌─────────────────────────────────────────────────────────────┐
│                      请求流程                                │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 发起API请求                                              │
│         │                                                    │
│         ▼                                                    │
│  ┌──────────────┐                                           │
│  │ 请求成功?     │──否──► 结束                               │
│  └──────┬───────┘                                           │
│         │是                                                  │
│         ▼                                                    │
│  ┌──────────────┐                                           │
│  │ 状态码=401?   │──否──► 结束                               │
│  └──────┬───────┘                                           │
│         │是                                                  │
│         ▼                                                    │
│  ┌──────────────────────────┐                               │
│  │ 正在刷新Token? (AtomicBoolean) │──是──► 等待并重试请求    │
│  └──────────┬───────────────┘                               │
│             │否                                              │
│             ▼                                                │
│  ┌──────────────────────────┐                               │
│  │ 调用 /user/refresh 接口   │                               │
│  └──────────┬───────────────┘                               │
│             │                                                │
│             ▼                                                │
│  ┌──────────────────────────┐                               │
│  │ 刷新成功?                 │──否──► 清除Token，跳转登录    │
│  └──────────┬───────────────┘                               │
│             │是                                              │
│             ▼                                                │
│  ┌──────────────────────────┐                               │
│  │ 保存新Token，重试原请求   │                               │
│  └──────────────────────────┘                               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### 关键实现点

1. **防止并发刷新**：使用 `AtomicBoolean` 或类似机制确保同一时间只有一个刷新请求
2. **请求队列**：刷新期间的其他请求应加入队列，刷新成功后依次重试
3. **刷新失败处理**：刷新失败后必须清除本地所有认证信息并跳转登录页
4. **Token存储安全**：Token应存储在加密的SharedPreferences或KeyStore中
5. **定期清理**：APP退出时应清除过期Token

#### 示例代码结构（Android/OkHttp）

```java
// AuthAuthenticator 实现
class AuthAuthenticator implements Authenticator {
    private AtomicBoolean isRefreshing = new AtomicBoolean(false);
    
    @Override
    public Request authenticate(Route route, Response response) {
        if (!shouldRefresh(response)) {
            return null;
        }
        
        if (isRefreshing.compareAndSet(false, true)) {
            try {
                // 调用刷新接口
                AuthResponse newToken = apiService.refreshToken(refreshToken);
                // 保存新Token
                tokenManager.saveToken(newToken.getToken());
                tokenManager.saveRefreshToken(newToken.getRefreshToken());
                // 通知等待的请求
                authCallbackManager.notifyTokenRefreshed(newToken);
                // 重试原请求
                return response.request().newBuilder()
                    .header("Authorization", "Bearer " + newToken.getToken())
                    .build();
            } catch (Exception e) {
                // 刷新失败，跳转登录
                authCallbackManager.notifyRefreshFailed();
                return null;
            } finally {
                isRefreshing.set(false);
            }
        } else {
            // 已有其他请求在刷新，等待刷新完成
            return waitForRefreshAndRetry(response.request());
        }
    }
}
```

### D. 分页规范

- 所有列表接口默认使用分页
- page 从 0 开始计数
- size 最大值为 50
- 建议首次加载 size=10，下拉加载更多时 size=20
- 客户端应在到达最后一页时停止加载更多

### E. 图片规格建议

| 用途   | 推荐尺寸       | 格式      | 大小限制   |
| ---- | ---------- | ------- | ------ |
| 帖子封面 | 600x400px  | JPG/PNG | ≤ 5MB  |
| 用户头像 | 200x200px  | PNG     | ≤ 2MB  |
| 详情大图 | 1200x800px | JPG/PNG | ≤ 10MB |

***

**文档版本**: v2.0

**最后更新**: 2026-04-13

**维护者**: Android开发团队
