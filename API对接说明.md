# API 对接完成说明

## ✅ 已完成的后端 API 接口

### 1. 用户管理接口

#### 1.1 用户列表（分页查询）
- **接口**: `GET /api/user/list`
- **参数**: 
  - `current`: 当前页码（默认1）
  - `size`: 每页大小（默认10）
  - `username`: 用户名（模糊查询，可选）
  - `nickname`: 昵称（模糊查询，可选）
  - `email`: 邮箱（精确查询，可选）
  - `phone`: 手机号（精确查询,可选）
  - `status`: 状态（可选）
- **返回**: 
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [...],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```
- **前端对接**: ✅ 已完成
- **文件**: `/frontend/src/views/user/index.vue`

#### 1.2 用户详情
- **接口**: `GET /api/user/{id}`
- **参数**: `id` - 用户ID（路径参数）
- **返回**: 用户详细信息（密码已清空）
- **前端对接**: ✅ 已完成

#### 1.3 用户注册
- **接口**: `POST /api/user/register`
- **参数**: 
```json
{
  "username": "用户名",
  "password": "密码",
  "nickname": "昵称",
  "email": "邮箱",
  "phone": "手机号",
  "status": 1
}
```
- **前端对接**: ✅ 已完成（用户管理新增功能）

#### 1.4 用户登录
- **接口**: `POST /api/user/login`
- **参数**: 
```json
{
  "username": "admin",
  "password": "admin123"
}
```
- **返回**: 
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzM4NCJ9..."
  }
}
```
- **前端对接**: ✅ 已完成
- **文件**: `/frontend/src/views/Login.vue`

#### 1.5 更新用户
- **接口**: `PUT /api/user/update`
- **参数**: 
```json
{
  "id": 1,
  "username": "admin",
  "nickname": "新昵称",
  "email": "new@example.com",
  "phone": "13900000000",
  "status": 1
}
```
- **前端对接**: ✅ 已完成

#### 1.6 删除用户
- **接口**: `DELETE /api/user/{id}`
- **参数**: `id` - 用户ID（路径参数）
- **前端对接**: ✅ 已完成

---

### 2. 角色管理接口

#### 2.1 角色列表（分页查询）
- **接口**: `GET /api/role/list`
- **参数**: 
  - `current`: 当前页码（默认1）
  - `size`: 每页大小（默认10）
  - `roleName`: 角色名称（模糊查询，可选）
  - `roleCode`: 角色编码（精确查询，可选）
  - `status`: 状态（可选）
- **返回**: 分页数据
- **前端对接**: ✅ 已完成
- **文件**: `/frontend/src/views/role/index.vue`

#### 2.2 角色详情
- **接口**: `GET /api/role/{id}`
- **参数**: `id` - 角色ID（路径参数）
- **前端对接**: ✅ 已完成

#### 2.3 新增角色
- **接口**: `POST /api/role`
- **参数**: 
```json
{
  "roleName": "测试角色",
  "roleCode": "TEST_ROLE",
  "description": "测试角色描述",
  "sort": 10,
  "status": 1
}
```
- **校验规则**:
  - `roleName`: 必填
  - `roleCode`: 必填，只能包含大写字母和下划线
  - 角色编码不能重复
- **前端对接**: ✅ 已完成

#### 2.4 更新角色
- **接口**: `PUT /api/role/{id}`
- **参数**: 
```json
{
  "id": 1,
  "roleName": "更新后的角色名",
  "roleCode": "UPDATED_ROLE",
  "description": "更新后的描述",
  "sort": 20,
  "status": 1
}
```
- **注意**: 修改角色编码时会检查新编码是否已被使用
- **前端对接**: ✅ 已完成

#### 2.5 删除角色
- **接口**: `DELETE /api/role/{id}`
- **参数**: `id` - 角色ID（路径参数）
- **前端对接**: ✅ 已完成

#### 2.6 批量删除角色
- **接口**: `DELETE /api/role/batch`
- **参数**: 
```json
[1, 2, 3]
```
- **前端对接**: ⏳ 待对接（前端暂未实现批量删除功能）

---

## 🔐 权限和认证

### JWT Token 认证
- **请求头**: `Authorization: Bearer {token}`
- **Token 过期时间**: 24小时
- **自动处理**: 
  - 前端请求拦截器自动添加 Token
  - 401 响应自动跳转登录页
  - Token 存储在 localStorage

### 公开接口（无需认证）
- `/` - 首页
- `/api/user/login` - 登录
- `/api/user/register` - 注册
- `/api/health` - 健康检查
- `/doc.html` - API 文档
- Swagger 相关路径

---

## 📦 数据库表结构

### sys_user 表
```sql
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    gender TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted TINYINT DEFAULT 0
);
```

### sys_role 表
```sql
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted TINYINT DEFAULT 0
);
```

---

## 🎯 前端对接情况

### 用户管理页面 (`/frontend/src/views/user/index.vue`)
- ✅ 列表展示（调用 `/api/user/list`）
- ✅ 搜索功能（用户名搜索）
- ✅ 分页功能
- ✅ 新增用户（调用 `/api/user/register`）
- ✅ 编辑用户（调用 `/api/user/update`）
- ✅ 删除用户（调用 `/api/user/{id}`）
- ✅ 查看详情（使用 el-descriptions 展示）

### 角色管理页面 (`/frontend/src/views/role/index.vue`)
- ✅ 列表展示（调用 `/api/role/list`）
- ✅ 搜索功能（角色名称搜索）
- ✅ 分页功能
- ✅ 新增角色（调用 `POST /api/role`）
- ✅ 编辑角色（调用 `PUT /api/role/{id}`）
- ✅ 删除角色（调用 `DELETE /api/role/{id}`）
- ✅ 查看详情（使用 el-descriptions 展示）

### 登录页面 (`/frontend/src/views/Login.vue`)
- ✅ 登录功能（调用 `/api/user/login`）
- ✅ Token 存储
- ✅ 自动跳转

---

## 🚀 测试建议

### 1. 登录测试
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 2. 用户列表测试
```bash
# 获取 token 后
curl -X GET "http://localhost:8080/api/user/list?current=1&size=10" \
  -H "Authorization: Bearer {your_token}"
```

### 3. 角色列表测试
```bash
curl -X GET "http://localhost:8080/api/role/list?current=1&size=10" \
  -H "Authorization: Bearer {your_token}"
```

### 4. 新增角色测试
```bash
curl -X POST http://localhost:8080/api/role \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your_token}" \
  -d '{
    "roleName": "测试角色",
    "roleCode": "TEST_ROLE",
    "description": "这是一个测试角色",
    "status": 1
  }'
```

---

## 📝 默认数据

### 默认用户
- 用户名: `admin`
- 密码: `admin123`
- 角色: 超级管理员

### 默认角色
1. **超级管理员** (SUPER_ADMIN)
2. **管理员** (ADMIN)
3. **普通用户** (USER)

---

## ⚠️ 注意事项

1. **密码加密**: 使用 BCrypt 加密，强度为 10
2. **逻辑删除**: 所有删除操作都是逻辑删除（deleted=1）
3. **数据校验**: 
   - 用户名：3-20字符
   - 密码：最少6位
   - 邮箱：必须符合邮箱格式
   - 手机号：必须是11位数字
   - 角色编码：只能包含大写字母和下划线
4. **唯一性约束**:
   - 用户名不能重复
   - 角色编码不能重复

---

## 🔧 后续优化建议

1. ⏳ 实现批量删除功能（前端）
2. ⏳ 添加用户角色关联功能
3. ⏳ 实现权限管理功能
4. ⏳ 添加操作日志记录
5. ⏳ 实现数据导出功能
6. ⏳ 添加头像上传功能

---

## 📚 相关文件

### 后端文件
- **控制器**: 
  - `/backend/src/main/java/com/wuxing/controller/UserController.java`
  - `/backend/src/main/java/com/wuxing/controller/RoleController.java`
- **服务**: 
  - `/backend/src/main/java/com/wuxing/service/UserService.java`
  - `/backend/src/main/java/com/wuxing/service/RoleService.java`
- **Mapper**: 
  - `/backend/src/main/java/com/wuxing/mapper/UserMapper.java`
  - `/backend/src/main/java/com/wuxing/mapper/RoleMapper.java`
- **实体**: 
  - `/backend/src/main/java/com/wuxing/entity/User.java`
  - `/backend/src/main/java/com/wuxing/entity/Role.java`
- **DTO**: 
  - `/backend/src/main/java/com/wuxing/dto/request/UserQueryRequest.java`
  - `/backend/src/main/java/com/wuxing/dto/request/RoleRequest.java`

### 前端文件
- **API**: 
  - `/frontend/src/api/user.js`
  - `/frontend/src/api/role.js`
- **页面**: 
  - `/frontend/src/views/user/index.vue`
  - `/frontend/src/views/role/index.vue`
  - `/frontend/src/views/Login.vue`
- **工具**: 
  - `/frontend/src/utils/request.js`
- **状态管理**: 
  - `/frontend/src/stores/user.js`
