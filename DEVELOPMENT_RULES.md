# 🚀 开发规则与架构规范

## 📋 项目概述
AI面试助手宠物养成游戏 - 前后端功能模块完整对应规范

## 🏗️ 架构原则

### 1. 模块化设计原则
- **单一职责原则**: 每个模块只负责一个特定功能域
- **高内聚低耦合**: 模块内部功能紧密相关，模块间依赖最小化
- **接口一致性**: 前后端API接口保持完全一致
- **状态同步**: 前端状态与后端数据实时同步

### 2. 抽象模块概念
每次开发新功能时，必须遵循以下抽象模块设计流程：

```
需求分析 → 抽象模块定义 → 后端接口设计 → 前端组件设计 → 数据流设计 → 测试验证
```

## 🔄 前后端功能模块对应关系

### 模块1: 宠物基础管理 (PetManagement)
**后端控制器**: `PetManagementController`
**前端组件**: `PetManagement.vue` (待创建)

#### 功能接口对应:
| 后端接口 | 前端功能 | 状态 | 组件 |
|---------|---------|------|------|
| `POST /api/pets/create` | 宠物创建表单 | ❌ 待实现 | `PetCreationForm.vue` |
| `GET /api/pets/{petId}` | 宠物详情展示 | ❌ 待实现 | `PetDetail.vue` |
| `GET /api/pets/player/{playerId}` | 宠物列表管理 | ❌ 待实现 | `PetList.vue` |
| `GET /api/pets/types` | 宠物类型选择 | ❌ 待实现 | `PetTypeSelector.vue` |
| `GET /api/pets/personalities` | 性格类型选择 | ❌ 待实现 | `PersonalitySelector.vue` |

### 模块2: 宠物交互操作 (PetInteraction)
**后端控制器**: `PetInteractionController`
**前端组件**: `PetInteraction.vue` (待创建)

#### 功能接口对应:
| 后端接口 | 前端功能 | 状态 | 组件 |
|---------|---------|------|------|
| `POST /api/pets/{petId}/feed` | 喂食操作 | ❌ 待实现 | `PetFeeding.vue` |
| `POST /api/pets/{petId}/play` | 玩耍操作 | ❌ 待实现 | `PetPlaying.vue` |
| `POST /api/pets/{petId}/clean` | 清洁操作 | ❌ 待实现 | `PetCleaning.vue` |
| `POST /api/pets/{petId}/rest` | 休息操作 | ❌ 待实现 | `PetResting.vue` |

### 模块3: 小游戏系统 (PetGame)
**后端控制器**: `PetGameController`
**前端组件**: `PetGame.vue` ✅ 已存在

#### 功能接口对应:
| 后端接口 | 前端功能 | 状态 | 组件 |
|---------|---------|------|------|
| `POST /api/pets/{petId}/minigames/start` | 游戏开始 | ❌ 待实现 | `MiniGameStarter.vue` |
| `POST /api/pets/{petId}/minigames/complete` | 游戏完成 | ❌ 待实现 | `MiniGameCompleter.vue` |

### 模块4: 成就系统 (PetAchievement)
**后端控制器**: `PetAchievementController`
**前端组件**: `PetAchievement.vue` (待创建)

#### 功能接口对应:
| 后端接口 | 前端功能 | 状态 | 组件 |
|---------|---------|------|------|
| `GET /api/pets/player/{playerId}/achievements` | 成就展示 | ❌ 待实现 | `AchievementDisplay.vue` |

### 模块5: 背包系统 (PetInventory)
**后端控制器**: `PetInventoryController`
**前端组件**: `PetInventory.vue` (待创建)

#### 功能接口对应:
| 后端接口 | 前端功能 | 状态 | 组件 |
|---------|---------|------|------|
| `GET /api/pets/player/{playerId}/inventory` | 背包管理 | ❌ 待实现 | `InventoryManager.vue` |

### 模块6: 统计信息 (PetStats)
**后端控制器**: `PetStatsController`
**前端组件**: `PetStats.vue` (待创建)

#### 功能接口对应:
| 后端接口 | 前端功能 | 状态 | 组件 |
|---------|---------|------|------|
| `GET /api/pets/player/{playerId}/stats` | 统计展示 | ❌ 待实现 | `StatsDisplay.vue` |

### 模块7: 系统功能 (PetSystem)
**后端控制器**: `PetSystemController`
**前端组件**: `PetSystem.vue` (待创建)

#### 功能接口对应:
| 后端接口 | 前端功能 | 状态 | 组件 |
|---------|---------|------|------|
| `GET /api/pets/health` | 系统状态 | ❌ 待实现 | `SystemStatus.vue` |

## 🎯 开发流程规范

### 阶段1: 抽象模块定义
```typescript
// 每个功能模块必须定义以下接口
interface ModuleInterface {
  // 1. 数据模型定义
  dataModels: {
    request: RequestType;
    response: ResponseType;
    state: StateType;
  };
  
  // 2. 业务逻辑定义
  businessLogic: {
    validation: ValidationRules;
    processing: ProcessingSteps;
    errorHandling: ErrorScenarios;
  };
  
  // 3. 用户交互定义
  userInteraction: {
    triggers: UserActions;
    feedback: SystemResponses;
    states: UIStates;
  };
}
```

### 阶段2: 后端接口实现
```java
// 遵循RESTful API设计原则
@RestController
@RequestMapping("/api/{module}")
public class ModuleController {
    
    // 1. 输入验证
    @PostMapping("/action")
    public ResponseEntity<ApiResponse<ResultType>> performAction(
        @Valid @RequestBody RequestType request) {
        // 2. 业务逻辑处理
        // 3. 结果返回
    }
}
```

### 阶段3: 前端组件实现
```vue
<!-- 遵循Vue 3 Composition API最佳实践 -->
<template>
  <div class="module-component">
    <!-- 1. 用户界面 -->
    <!-- 2. 状态展示 -->
    <!-- 3. 交互操作 -->
  </div>
</template>

<script setup lang="ts">
// 1. 数据状态管理
// 2. 业务逻辑处理
// 3. API调用封装
// 4. 错误处理
</script>
```

### 阶段4: 数据流设计
```typescript
// 定义清晰的数据流向
interface DataFlow {
  // 1. 用户操作 → 前端状态更新
  userAction: UserAction;
  
  // 2. 前端状态 → API请求
  apiRequest: ApiRequest;
  
  // 3. 后端处理 → 响应数据
  apiResponse: ApiResponse;
  
  // 4. 响应数据 → 前端状态更新
  stateUpdate: StateUpdate;
  
  // 5. 状态更新 → UI更新
  uiUpdate: UIUpdate;
}
```

## 📊 当前实现状态

### 后端状态: ✅ 100% 完成
- 7个控制器全部创建完成
- 15个API接口全部实现
- 业务逻辑完整

### 前端状态: ❌ 0% 完成
- 现有组件与后端功能不匹配
- 需要重新设计组件架构
- 数据流需要重新规划

## 🚧 下一步开发计划

### 优先级1: 核心功能模块
1. **PetManagement** - 宠物基础管理
2. **PetInteraction** - 宠物交互操作
3. **PetGame** - 小游戏系统

### 优先级2: 辅助功能模块
4. **PetAchievement** - 成就系统
5. **PetInventory** - 背包系统
6. **PetStats** - 统计信息

### 优先级3: 系统功能模块
7. **PetSystem** - 系统功能

## 🔍 质量检查清单

每次完成功能模块后，必须检查：

- [ ] 后端接口功能完整
- [ ] 前端组件功能完整
- [ ] 前后端数据流一致
- [ ] 错误处理完善
- [ ] 用户体验流畅
- [ ] 代码质量达标
- [ ] 测试覆盖充分
- [ ] 文档更新完整

## 📝 开发注意事项

1. **命名规范**: 前后端使用一致的命名规范
2. **错误处理**: 统一的错误处理机制
3. **状态管理**: 清晰的状态管理策略
4. **性能优化**: 考虑接口性能和用户体验
5. **安全考虑**: 输入验证和权限控制
6. **可扩展性**: 为未来功能预留扩展空间

## 🎯 目标成果

完成所有模块后，系统将具备：
- 完整的前后端功能对应
- 清晰的模块化架构
- 良好的用户体验
- 可维护的代码结构
- 可扩展的系统设计
