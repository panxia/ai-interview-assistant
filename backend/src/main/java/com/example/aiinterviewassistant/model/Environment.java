package com.example.aiinterviewassistant.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 环境和天气系统
 * 管理游戏环境、天气变化和房间装饰
 */
public class Environment {
    
    // 当前环境设置
    private Room currentRoom = Room.LIVING_ROOM;
    private Weather currentWeather = Weather.SUNNY;
    private TimeOfDay timeOfDay = TimeOfDay.MORNING;
    
    // 环境装饰
    private Map<String, Furniture> furnitureMap = new HashMap<>();
    private Map<String, Decoration> decorationMap = new HashMap<>();
    private List<InteractableObject> interactableObjects = new ArrayList<>();
    
    // 天气系统参数
    private long lastWeatherChange = System.currentTimeMillis();
    private long weatherChangeDuration = 3600000; // 1小时换一次天气
    private boolean dynamicWeatherEnabled = true;
    
    // 环境效果
    private float lightIntensity = 1.0f;
    private String ambientSound = "default";
    private Map<String, Float> environmentEffects = new HashMap<>();
    
    /**
     * 房间枚举
     */
    public enum Room {
        LIVING_ROOM("客厅", "living", 100, 80),
        BEDROOM("卧室", "bedroom", 80, 60),
        GARDEN("花园", "garden", 150, 120),
        PLAYGROUND("游乐场", "playground", 200, 150),
        KITCHEN("厨房", "kitchen", 60, 50),
        BATHROOM("浴室", "bathroom", 40, 40),
        BALCONY("阳台", "balcony", 60, 40),
        STUDY("书房", "study", 70, 60);
        
        private final String displayName;
        private final String code;
        private final float width;
        private final float height;
        
        Room(String displayName, String code, float width, float height) {
            this.displayName = displayName;
            this.code = code;
            this.width = width;
            this.height = height;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
        public float getWidth() { return width; }
        public float getHeight() { return height; }
    }
    
    /**
     * 天气枚举
     */
    public enum Weather {
        SUNNY("晴天", "sunny", 1.2f, "宠物更活跃"),
        CLOUDY("多云", "cloudy", 1.0f, "适合休息"),
        RAINY("雨天", "rainy", 0.8f, "宠物想待在室内"),
        SNOWY("雪天", "snowy", 0.7f, "宠物好奇但行动缓慢"),
        WINDY("大风", "windy", 0.9f, "宠物有点不安"),
        FOGGY("雾天", "foggy", 0.85f, "视野受限"),
        STORMY("暴风雨", "stormy", 0.6f, "宠物害怕躲起来"),
        RAINBOW("彩虹", "rainbow", 1.5f, "宠物兴奋追彩虹"),
        AURORA("极光", "aurora", 1.3f, "稀有美景");
        
        private final String displayName;
        private final String code;
        private final float activityModifier;
        private final String effect;
        
        Weather(String displayName, String code, float activityModifier, String effect) {
            this.displayName = displayName;
            this.code = code;
            this.activityModifier = activityModifier;
            this.effect = effect;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
        public float getActivityModifier() { return activityModifier; }
        public String getEffect() { return effect; }
    }
    
    /**
     * 时间段枚举
     */
    public enum TimeOfDay {
        DAWN("黎明", "dawn", 0.6f),
        MORNING("早晨", "morning", 1.0f),
        NOON("中午", "noon", 1.2f),
        AFTERNOON("下午", "afternoon", 1.0f),
        EVENING("傍晚", "evening", 0.8f),
        NIGHT("夜晚", "night", 0.5f),
        MIDNIGHT("深夜", "midnight", 0.3f);
        
        private final String displayName;
        private final String code;
        private final float lightLevel;
        
        TimeOfDay(String displayName, String code, float lightLevel) {
            this.displayName = displayName;
            this.code = code;
            this.lightLevel = lightLevel;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
        public float getLightLevel() { return lightLevel; }
    }
    
    /**
     * 家具类
     */
    public static class Furniture {
        private String id;
        private String name;
        private FurnitureType type;
        private PetAnimation.Position position;
        private float width;
        private float height;
        private boolean isInteractable;
        private int price;
        private int comfortLevel;
        
        public enum FurnitureType {
            SOFA("沙发", 200),
            BED("床", 300),
            TABLE("桌子", 150),
            CHAIR("椅子", 100),
            CABINET("柜子", 250),
            PLANT("植物", 50),
            PET_BED("宠物床", 100),
            FOOD_BOWL("食盆", 30),
            WATER_BOWL("水盆", 20),
            LITTER_BOX("猫砂盆", 40),
            SCRATCHING_POST("猫抓板", 60),
            TOY_BOX("玩具箱", 80);
            
            private final String displayName;
            private final int basePrice;
            
            FurnitureType(String displayName, int basePrice) {
                this.displayName = displayName;
                this.basePrice = basePrice;
            }
            
            public String getDisplayName() { return displayName; }
            public int getBasePrice() { return basePrice; }
        }
        
        public Furniture(String id, String name, FurnitureType type) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.price = type.getBasePrice();
            this.isInteractable = true;
            this.comfortLevel = 50;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public String getName() { return name; }
        public FurnitureType getType() { return type; }
        public PetAnimation.Position getPosition() { return position; }
        public void setPosition(PetAnimation.Position position) { this.position = position; }
        public boolean isInteractable() { return isInteractable; }
        public int getPrice() { return price; }
        public int getComfortLevel() { return comfortLevel; }
    }
    
    /**
     * 装饰品类
     */
    public static class Decoration {
        private String id;
        private String name;
        private DecorationType type;
        private PetAnimation.Position position;
        private int price;
        private String effect;
        
        public enum DecorationType {
            PAINTING("画框", 100),
            RUG("地毯", 150),
            CURTAIN("窗帘", 80),
            LAMP("灯具", 120),
            CLOCK("时钟", 60),
            MIRROR("镜子", 90),
            VASE("花瓶", 40),
            PHOTO_FRAME("相框", 30),
            WALL_STICKER("墙贴", 20),
            WIND_CHIME("风铃", 50);
            
            private final String displayName;
            private final int basePrice;
            
            DecorationType(String displayName, int basePrice) {
                this.displayName = displayName;
                this.basePrice = basePrice;
            }
            
            public String getDisplayName() { return displayName; }
            public int getBasePrice() { return basePrice; }
        }
        
        public Decoration(String id, String name, DecorationType type) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.price = type.getBasePrice();
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public String getName() { return name; }
        public DecorationType getType() { return type; }
        public PetAnimation.Position getPosition() { return position; }
        public void setPosition(PetAnimation.Position position) { this.position = position; }
        public int getPrice() { return price; }
        public String getEffect() { return effect; }
    }
    
    /**
     * 可交互物品类
     */
    public static class InteractableObject {
        private String id;
        private String name;
        private ObjectType type;
        private PetAnimation.Position position;
        private boolean isActive;
        private String interaction;
        
        public enum ObjectType {
            BALL("球", "play"),
            MOUSE_TOY("老鼠玩具", "chase"),
            LASER_POINTER("激光笔", "follow"),
            SLIDE("滑梯", "slide"),
            TUNNEL("隧道", "explore"),
            FEATHER_TOY("羽毛玩具", "catch"),
            BUBBLE_MACHINE("泡泡机", "pop"),
            MUSIC_BOX("音乐盒", "dance");
            
            private final String displayName;
            private final String action;
            
            ObjectType(String displayName, String action) {
                this.displayName = displayName;
                this.action = action;
            }
            
            public String getDisplayName() { return displayName; }
            public String getAction() { return action; }
        }
        
        public InteractableObject(String id, String name, ObjectType type) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.isActive = false;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public String getName() { return name; }
        public ObjectType getType() { return type; }
        public PetAnimation.Position getPosition() { return position; }
        public void setPosition(PetAnimation.Position position) { this.position = position; }
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        public String getInteraction() { return interaction; }
    }
    
    /**
     * 更新天气（自动或手动）
     */
    public void updateWeather() {
        if (!dynamicWeatherEnabled) {
            return;
        }
        
        long now = System.currentTimeMillis();
        if (now - lastWeatherChange >= weatherChangeDuration) {
            // 随机切换天气
            Weather[] weathers = Weather.values();
            Weather newWeather;
            do {
                // 特殊天气出现概率较低
                if (Math.random() < 0.1) {
                    // 10%概率出现特殊天气
                    newWeather = Math.random() < 0.5 ? Weather.RAINBOW : Weather.AURORA;
                } else {
                    // 90%概率出现普通天气
                    int index = (int) (Math.random() * 7); // 前7种普通天气
                    newWeather = weathers[index];
                }
            } while (newWeather == currentWeather);
            
            setWeather(newWeather);
            lastWeatherChange = now;
        }
    }
    
    /**
     * 设置天气
     */
    public void setWeather(Weather weather) {
        this.currentWeather = weather;
        
        // 更新环境效果
        environmentEffects.put("activity_modifier", weather.getActivityModifier());
        
        // 更新光照
        switch (weather) {
            case SUNNY -> lightIntensity = 1.2f;
            case CLOUDY -> lightIntensity = 0.8f;
            case RAINY, STORMY -> lightIntensity = 0.6f;
            case SNOWY -> lightIntensity = 0.9f;
            case FOGGY -> lightIntensity = 0.7f;
            case RAINBOW -> lightIntensity = 1.3f;
            case AURORA -> lightIntensity = 1.1f;
            default -> lightIntensity = 1.0f;
        }
        
        // 更新环境音效
        switch (weather) {
            case RAINY -> ambientSound = "rain";
            case STORMY -> ambientSound = "storm";
            case WINDY -> ambientSound = "wind";
            case SNOWY -> ambientSound = "snow";
            default -> ambientSound = "default";
        }
    }
    
    /**
     * 更新时间段
     */
    public void updateTimeOfDay() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        
        if (hour >= 5 && hour < 7) {
            timeOfDay = TimeOfDay.DAWN;
        } else if (hour >= 7 && hour < 11) {
            timeOfDay = TimeOfDay.MORNING;
        } else if (hour >= 11 && hour < 14) {
            timeOfDay = TimeOfDay.NOON;
        } else if (hour >= 14 && hour < 18) {
            timeOfDay = TimeOfDay.AFTERNOON;
        } else if (hour >= 18 && hour < 20) {
            timeOfDay = TimeOfDay.EVENING;
        } else if (hour >= 20 && hour < 24) {
            timeOfDay = TimeOfDay.NIGHT;
        } else {
            timeOfDay = TimeOfDay.MIDNIGHT;
        }
        
        // 更新光照强度
        lightIntensity *= timeOfDay.getLightLevel();
    }
    
    /**
     * 添加家具
     */
    public boolean addFurniture(Furniture furniture, PetAnimation.Position position) {
        if (canPlaceObject(position, furniture.width, furniture.height)) {
            furniture.setPosition(position);
            furnitureMap.put(furniture.getId(), furniture);
            return true;
        }
        return false;
    }
    
    /**
     * 检查是否可以放置物品
     */
    private boolean canPlaceObject(PetAnimation.Position position, float width, float height) {
        // 检查是否在房间范围内
        if (position.x < 0 || position.x + width > currentRoom.getWidth() ||
            position.y < 0 || position.y + height > currentRoom.getHeight()) {
            return false;
        }
        
        // 检查是否与其他物品重叠
        for (Furniture f : furnitureMap.values()) {
            if (isOverlapping(position, width, height, f.position, f.width, f.height)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检查两个矩形是否重叠
     */
    private boolean isOverlapping(PetAnimation.Position pos1, float w1, float h1,
                                 PetAnimation.Position pos2, float w2, float h2) {
        return pos1.x < pos2.x + w2 && pos1.x + w1 > pos2.x &&
               pos1.y < pos2.y + h2 && pos1.y + h1 > pos2.y;
    }
    
    /**
     * 获取环境描述
     */
    public String getEnvironmentDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append("你在").append(currentRoom.getDisplayName()).append("，");
        desc.append("现在是").append(timeOfDay.getDisplayName()).append("，");
        desc.append("天气").append(currentWeather.getDisplayName()).append("。");
        
        if (currentWeather == Weather.RAINBOW) {
            desc.append("天空中出现了美丽的彩虹！");
        } else if (currentWeather == Weather.AURORA) {
            desc.append("极光在夜空中舞动，非常壮观！");
        }
        
        return desc.toString();
    }
    
    // Getters and Setters
    public Room getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Room room) { this.currentRoom = room; }
    
    public Weather getCurrentWeather() { return currentWeather; }
    
    public TimeOfDay getTimeOfDay() { return timeOfDay; }
    
    public float getLightIntensity() { return lightIntensity; }
    
    public String getAmbientSound() { return ambientSound; }
    
    public Map<String, Furniture> getFurnitureMap() { return furnitureMap; }
    
    public Map<String, Decoration> getDecorationMap() { return decorationMap; }
    
    public List<InteractableObject> getInteractableObjects() { return interactableObjects; }
    
    public boolean isDynamicWeatherEnabled() { return dynamicWeatherEnabled; }
    public void setDynamicWeatherEnabled(boolean enabled) { this.dynamicWeatherEnabled = enabled; }
}