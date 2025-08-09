package com.example.aiinterviewassistant.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 环境系统 - 房间装饰和动态天气
 * 提供丰富的环境互动和装饰功能
 */
public class Environment {

    /**
     * 房间类型枚举
     */
    public enum RoomType {
        LIVING_ROOM("客厅", "🛋️", "舒适的休息空间"),
        BEDROOM("卧室", "🛏️", "安静的睡眠环境"),
        KITCHEN("厨房", "🍽️", "美食制作区域"),
        BATHROOM("浴室", "🛁", "清洁护理中心"),
        GARDEN("花园", "🌸", "自然户外空间"),
        PLAYGROUND("游戏室", "🎮", "娱乐活动专区");

        private final String displayName;
        private final String emoji;
        private final String description;

        RoomType(String displayName, String emoji, String description) {
            this.displayName = displayName;
            this.emoji = emoji;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getEmoji() { return emoji; }
        public String getDescription() { return description; }
        public String getName() { return this.name(); }
    }

    /**
     * 天气类型枚举
     */
    public enum WeatherType {
        SUNNY("晴天", "☀️", "阳光明媚的好天气", 1.2, 1.0, 0.9, 1.1),
        CLOUDY("多云", "⛅", "云朵飘飘的天空", 1.0, 1.0, 1.0, 1.0),
        RAINY("雨天", "🌧️", "雨滴答答的室内天", 0.8, 1.3, 1.1, 0.9),
        SNOWY("雪天", "❄️", "雪花纷飞的冬日", 0.9, 1.1, 1.0, 0.8),
        STORMY("雷暴", "⛈️", "雷声隆隆的暴风雨", 0.6, 1.4, 1.2, 0.7),
        NIGHT("夜晚", "🌙", "宁静的夜晚时光", 0.7, 1.0, 1.0, 1.3);

        private final String displayName;
        private final String emoji;
        private final String description;
        private final double activityMultiplier; // 活动度影响
        private final double sleepMultiplier;    // 睡眠倾向影响
        private final double hungerMultiplier;   // 饥饿度影响
        private final double affectionMultiplier; // 亲近度影响

        WeatherType(String displayName, String emoji, String description, 
                   double activityMultiplier, double sleepMultiplier, 
                   double hungerMultiplier, double affectionMultiplier) {
            this.displayName = displayName;
            this.emoji = emoji;
            this.description = description;
            this.activityMultiplier = activityMultiplier;
            this.sleepMultiplier = sleepMultiplier;
            this.hungerMultiplier = hungerMultiplier;
            this.affectionMultiplier = affectionMultiplier;
        }

        public String getDisplayName() { return displayName; }
        public String getEmoji() { return emoji; }
        public String getDescription() { return description; }
        public double getActivityMultiplier() { return activityMultiplier; }
        public double getSleepMultiplier() { return sleepMultiplier; }
        public double getHungerMultiplier() { return hungerMultiplier; }
        public double getAffectionMultiplier() { return affectionMultiplier; }
        public String getName() { return this.name(); }
    }

    /**
     * 装饰物品类
     */
    public static class DecorationItem {
        private String id;
        private String name;
        private String emoji;
        private String category; // furniture, toy, decoration, functional
        private RoomType suitableRoom;
        private int cost;
        private Map<String, Double> effects; // 对宠物属性的影响
        private boolean isUnlocked;
        private int unlockLevel;

        public DecorationItem() {
            this.id = UUID.randomUUID().toString();
            this.effects = new HashMap<>();
            this.isUnlocked = false;
        }

        public DecorationItem(String name, String emoji, String category, RoomType suitableRoom, int cost) {
            this();
            this.name = name;
            this.emoji = emoji;
            this.category = category;
            this.suitableRoom = suitableRoom;
            this.cost = cost;
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmoji() { return emoji; }
        public void setEmoji(String emoji) { this.emoji = emoji; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public RoomType getSuitableRoom() { return suitableRoom; }
        public void setSuitableRoom(RoomType suitableRoom) { this.suitableRoom = suitableRoom; }
        public int getCost() { return cost; }
        public void setCost(int cost) { this.cost = cost; }
        public Map<String, Double> getEffects() { return effects; }
        public void setEffects(Map<String, Double> effects) { this.effects = effects; }
        public boolean isUnlocked() { return isUnlocked; }
        public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
        public int getUnlockLevel() { return unlockLevel; }
        public void setUnlockLevel(int unlockLevel) { this.unlockLevel = unlockLevel; }
    }

    /**
     * 房间配置类
     */
    public static class RoomConfiguration {
        private RoomType roomType;
        private List<PlacedItem> placedItems;
        private String theme; // default, cozy, modern, nature
        private Map<String, Object> ambientEffects; // 环境效果

        public RoomConfiguration(RoomType roomType) {
            this.roomType = roomType;
            this.placedItems = new ArrayList<>();
            this.theme = "default";
            this.ambientEffects = new HashMap<>();
        }

        // Getters and Setters
        public RoomType getRoomType() { return roomType; }
        public void setRoomType(RoomType roomType) { this.roomType = roomType; }
        public List<PlacedItem> getPlacedItems() { return placedItems; }
        public void setPlacedItems(List<PlacedItem> placedItems) { this.placedItems = placedItems; }
        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }
        public Map<String, Object> getAmbientEffects() { return ambientEffects; }
        public void setAmbientEffects(Map<String, Object> ambientEffects) { this.ambientEffects = ambientEffects; }
    }

    /**
     * 已放置的物品类
     */
    public static class PlacedItem {
        private String itemId;
        private DecorationItem item;
        private double x;
        private double y;
        private double rotation; // 旋转角度
        private double scale;    // 缩放比例
        private LocalDateTime placedTime;

        public PlacedItem(DecorationItem item, double x, double y) {
            this.itemId = item.getId();
            this.item = item;
            this.x = x;
            this.y = y;
            this.rotation = 0;
            this.scale = 1.0;
            this.placedTime = LocalDateTime.now();
        }

        // Getters and Setters
        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
        public DecorationItem getItem() { return item; }
        public void setItem(DecorationItem item) { this.item = item; }
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
        public double getRotation() { return rotation; }
        public void setRotation(double rotation) { this.rotation = rotation; }
        public double getScale() { return scale; }
        public void setScale(double scale) { this.scale = scale; }
        public LocalDateTime getPlacedTime() { return placedTime; }
        public void setPlacedTime(LocalDateTime placedTime) { this.placedTime = placedTime; }
    }

    /**
     * 天气状态类
     */
    public static class WeatherState {
        private WeatherType currentWeather;
        private WeatherType nextWeather;
        private LocalDateTime lastChange;
        private LocalDateTime nextChange;
        private int temperature; // 温度 (°C)
        private int humidity;    // 湿度 (%)
        private List<String> specialEvents; // 特殊天气事件

        public WeatherState() {
            this.currentWeather = WeatherType.SUNNY;
            this.lastChange = LocalDateTime.now();
            this.nextChange = LocalDateTime.now().plusHours(2); // 2小时后变天
            this.temperature = 22;
            this.humidity = 60;
            this.specialEvents = new ArrayList<>();
        }

        // Getters and Setters
        public WeatherType getCurrentWeather() { return currentWeather; }
        public void setCurrentWeather(WeatherType currentWeather) { this.currentWeather = currentWeather; }
        public WeatherType getNextWeather() { return nextWeather; }
        public void setNextWeather(WeatherType nextWeather) { this.nextWeather = nextWeather; }
        public LocalDateTime getLastChange() { return lastChange; }
        public void setLastChange(LocalDateTime lastChange) { this.lastChange = lastChange; }
        public LocalDateTime getNextChange() { return nextChange; }
        public void setNextChange(LocalDateTime nextChange) { this.nextChange = nextChange; }
        public int getTemperature() { return temperature; }
        public void setTemperature(int temperature) { this.temperature = temperature; }
        public int getHumidity() { return humidity; }
        public void setHumidity(int humidity) { this.humidity = humidity; }
        public List<String> getSpecialEvents() { return specialEvents; }
        public void setSpecialEvents(List<String> specialEvents) { this.specialEvents = specialEvents; }
    }

    /**
     * 环境管理器类
     */
    public static class EnvironmentManager {
        private Map<RoomType, RoomConfiguration> rooms;
        private WeatherState weatherState;
        private List<DecorationItem> availableDecorations;

        public EnvironmentManager() {
            this.rooms = new HashMap<>();
            this.weatherState = new WeatherState();
            this.availableDecorations = new ArrayList<>();
            initializeDefaultRooms();
            initializeDecorations();
        }

        /**
         * 初始化默认房间
         */
        private void initializeDefaultRooms() {
            for (RoomType roomType : RoomType.values()) {
                rooms.put(roomType, new RoomConfiguration(roomType));
            }
        }

        /**
         * 初始化装饰物品
         */
        private void initializeDecorations() {
            // 家具类
            addDecoration("舒适沙发", "🛋️", "furniture", RoomType.LIVING_ROOM, 50, 
                         Map.of("comfort", 0.2, "energy_recovery", 0.15));
            addDecoration("温馨床铺", "🛏️", "furniture", RoomType.BEDROOM, 80, 
                         Map.of("sleep_quality", 0.3, "energy_recovery", 0.25));
            addDecoration("餐桌", "🍽️", "furniture", RoomType.KITCHEN, 40, 
                         Map.of("meal_satisfaction", 0.2));
            addDecoration("豪华浴缸", "🛁", "furniture", RoomType.BATHROOM, 100, 
                         Map.of("cleanliness_bonus", 0.3));

            // 玩具类
            addDecoration("小球", "🎾", "toy", RoomType.LIVING_ROOM, 15, 
                         Map.of("playfulness", 0.15, "happiness", 0.1));
            addDecoration("激光笔", "🔴", "toy", RoomType.LIVING_ROOM, 25, 
                         Map.of("activity", 0.2, "curiosity", 0.15));
            addDecoration("滑梯", "🛝", "toy", RoomType.PLAYGROUND, 120, 
                         Map.of("playfulness", 0.3, "activity", 0.25));

            // 装饰类
            addDecoration("美丽花束", "💐", "decoration", RoomType.LIVING_ROOM, 30, 
                         Map.of("mood", 0.1, "environment_beauty", 0.2));
            addDecoration("温馨画框", "🖼️", "decoration", RoomType.BEDROOM, 35, 
                         Map.of("comfort", 0.1, "relaxation", 0.15));
            addDecoration("地毯", "🔷", "decoration", RoomType.LIVING_ROOM, 45, 
                         Map.of("comfort", 0.15, "warmth", 0.1));

            // 功能类
            addDecoration("食盆", "🥣", "functional", RoomType.KITCHEN, 20, 
                         Map.of("eating_efficiency", 0.2));
            addDecoration("水盆", "💧", "functional", RoomType.KITCHEN, 15, 
                         Map.of("hydration", 0.15));
            addDecoration("猫砂盆", "📦", "functional", RoomType.BATHROOM, 25, 
                         Map.of("cleanliness_maintenance", 0.2));
        }

        private void addDecoration(String name, String emoji, String category, 
                                 RoomType room, int cost, Map<String, Double> effects) {
            DecorationItem item = new DecorationItem(name, emoji, category, room, cost);
            item.setEffects(effects);
            item.setUnlocked(true); // 默认解锁，后续可根据等级限制
            availableDecorations.add(item);
        }

        /**
         * 更新天气
         */
        public void updateWeather() {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(weatherState.getNextChange())) {
                // 随机选择新天气
                WeatherType[] weathers = WeatherType.values();
                Random random = new Random();
                WeatherType newWeather = weathers[random.nextInt(weathers.length)];
                
                weatherState.setCurrentWeather(newWeather);
                weatherState.setLastChange(now);
                weatherState.setNextChange(now.plusHours(random.nextInt(3) + 1)); // 1-3小时后变天
                
                // 根据天气调整温度和湿度
                updateTemperatureAndHumidity(newWeather);
                
                // 检查特殊天气事件
                checkSpecialWeatherEvents(newWeather);
            }
        }

        private void updateTemperatureAndHumidity(WeatherType weather) {
            Random random = new Random();
            switch (weather) {
                case SUNNY -> {
                    weatherState.setTemperature(22 + random.nextInt(8)); // 22-30°C
                    weatherState.setHumidity(40 + random.nextInt(20)); // 40-60%
                }
                case RAINY -> {
                    weatherState.setTemperature(15 + random.nextInt(10)); // 15-25°C
                    weatherState.setHumidity(70 + random.nextInt(25)); // 70-95%
                }
                case SNOWY -> {
                    weatherState.setTemperature(-5 + random.nextInt(10)); // -5-5°C
                    weatherState.setHumidity(80 + random.nextInt(15)); // 80-95%
                }
                case NIGHT -> {
                    weatherState.setTemperature(18 + random.nextInt(8)); // 18-26°C
                    weatherState.setHumidity(50 + random.nextInt(30)); // 50-80%
                }
                default -> {
                    weatherState.setTemperature(20 + random.nextInt(10)); // 20-30°C
                    weatherState.setHumidity(50 + random.nextInt(30)); // 50-80%
                }
            }
        }

        private void checkSpecialWeatherEvents(WeatherType weather) {
            Random random = new Random();
            weatherState.getSpecialEvents().clear();
            
            if (weather == WeatherType.RAINY && random.nextDouble() < 0.3) {
                weatherState.getSpecialEvents().add("rainbow"); // 30%概率出现彩虹
            }
            if (weather == WeatherType.STORMY && random.nextDouble() < 0.1) {
                weatherState.getSpecialEvents().add("lightning_show"); // 10%概率特殊闪电秀
            }
            if (weather == WeatherType.NIGHT && random.nextDouble() < 0.05) {
                weatherState.getSpecialEvents().add("meteor_shower"); // 5%概率流星雨
            }
            if (weather == WeatherType.SNOWY && random.nextDouble() < 0.02) {
                weatherState.getSpecialEvents().add("aurora"); // 2%概率极光
            }
        }

        /**
         * 计算房间对宠物的环境效果
         */
        public Map<String, Double> calculateRoomEffects(RoomType roomType) {
            Map<String, Double> totalEffects = new HashMap<>();
            RoomConfiguration room = rooms.get(roomType);
            
            if (room != null) {
                // 累计所有装饰物的效果
                for (PlacedItem placedItem : room.getPlacedItems()) {
                    DecorationItem item = placedItem.getItem();
                    for (Map.Entry<String, Double> effect : item.getEffects().entrySet()) {
                        totalEffects.merge(effect.getKey(), effect.getValue(), Double::sum);
                    }
                }
                
                // 应用天气效果
                WeatherType weather = weatherState.getCurrentWeather();
                totalEffects.put("activity_multiplier", weather.getActivityMultiplier());
                totalEffects.put("sleep_multiplier", weather.getSleepMultiplier());
                totalEffects.put("hunger_multiplier", weather.getHungerMultiplier());
                totalEffects.put("affection_multiplier", weather.getAffectionMultiplier());
            }
            
            return totalEffects;
        }

        // Getters and Setters
        public Map<RoomType, RoomConfiguration> getRooms() { return rooms; }
        public WeatherState getWeatherState() { return weatherState; }
        public List<DecorationItem> getAvailableDecorations() { return availableDecorations; }
        
        public void setRooms(Map<RoomType, RoomConfiguration> rooms) { this.rooms = rooms; }
        public void setWeatherState(WeatherState weatherState) { this.weatherState = weatherState; }
        public void setAvailableDecorations(List<DecorationItem> availableDecorations) { 
            this.availableDecorations = availableDecorations; 
        }
    }
}