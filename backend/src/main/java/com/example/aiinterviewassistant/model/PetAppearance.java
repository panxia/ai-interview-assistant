package com.example.aiinterviewassistant.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 宠物外观配置类
 * 支持完整的宠物外观自定义系统
 */
public class PetAppearance {
    
    // 身体部位配置
    private BodyParts bodyParts;
    
    // 颜色系统
    private ColorScheme colorScheme;
    
    // 图案系统
    private PatternConfig patternConfig;
    
    // 装饰系统
    private AccessoryConfig accessoryConfig;
    
    // 身体比例
    private BodyProportions bodyProportions;

    public PetAppearance() {
        this.bodyParts = new BodyParts();
        this.colorScheme = new ColorScheme();
        this.patternConfig = new PatternConfig();
        this.accessoryConfig = new AccessoryConfig();
        this.bodyProportions = new BodyProportions();
    }

    /**
     * 根据宠物类型设置默认外观
     */
    public void setDefaultAppearanceForType(PetType type) {
        switch (type) {
            case CAT -> {
                bodyParts.setHeadShape("round");
                bodyParts.setEarType("pointed");
                bodyParts.setEyeType("large");
                bodyParts.setMouthType("small");
                colorScheme.setPrimaryColor("#FF8C00");
                colorScheme.setSecondaryColor("#FFA500");
                patternConfig.setType("stripes");
                patternConfig.setColor("#FF7F00");
            }
            case DOG -> {
                bodyParts.setHeadShape("oval");
                bodyParts.setEarType("floppy");
                bodyParts.setEyeType("friendly");
                bodyParts.setMouthType("open");
                colorScheme.setPrimaryColor("#8B4513");
                colorScheme.setSecondaryColor("#DEB887");
                patternConfig.setType("spots");
                patternConfig.setColor("#654321");
            }
            case RABBIT -> {
                bodyParts.setHeadShape("oval");
                bodyParts.setEarType("long");
                bodyParts.setEyeType("large");
                bodyParts.setMouthType("small");
                colorScheme.setPrimaryColor("#FFFFFF");
                colorScheme.setSecondaryColor("#F0F0F0");
                patternConfig.setType("none");
            }
            case HAMSTER -> {
                bodyParts.setHeadShape("round");
                bodyParts.setEarType("round");
                bodyParts.setEyeType("small");
                bodyParts.setMouthType("tiny");
                colorScheme.setPrimaryColor("#F4A460");
                colorScheme.setSecondaryColor("#DEB887");
                patternConfig.setType("cheek_pouches");
                patternConfig.setColor("#FFE4B5");
            }
            case DRAGON -> {
                bodyParts.setHeadShape("triangular");
                bodyParts.setEarType("horn");
                bodyParts.setEyeType("mysterious");
                bodyParts.setMouthType("wise");
                colorScheme.setPrimaryColor("#9932CC");
                colorScheme.setSecondaryColor("#8A2BE2");
                patternConfig.setType("scales");
                patternConfig.setColor("#4B0082");
            }
            case PANDA -> {
                bodyParts.setHeadShape("round");
                bodyParts.setEarType("round");
                bodyParts.setEyeType("sleepy");
                bodyParts.setMouthType("content");
                colorScheme.setPrimaryColor("#FFFFFF");
                colorScheme.setSecondaryColor("#000000");
                patternConfig.setType("panda_markings");
                patternConfig.setColor("#000000");
            }
            case PENGUIN -> {
                bodyParts.setHeadShape("oval");
                bodyParts.setEarType("none");
                bodyParts.setEyeType("round");
                bodyParts.setMouthType("beak");
                colorScheme.setPrimaryColor("#000000");
                colorScheme.setSecondaryColor("#FFFFFF");
                patternConfig.setType("tuxedo");
                patternConfig.setColor("#FFA500");
            }
        }
    }

    // 身体部位配置类
    public static class BodyParts {
        private String headShape = "round"; // round, oval, square, triangular
        private String earType = "pointed"; // pointed, round, floppy, long, horn, none
        private String eyeType = "large"; // large, small, sleepy, mysterious, friendly
        private String mouthType = "small"; // small, open, tiny, wise, content, beak
        private String tailType = "normal"; // normal, fluffy, long, short, none
        
        // Getters and Setters
        public String getHeadShape() { return headShape; }
        public void setHeadShape(String headShape) { this.headShape = headShape; }
        
        public String getEarType() { return earType; }
        public void setEarType(String earType) { this.earType = earType; }
        
        public String getEyeType() { return eyeType; }
        public void setEyeType(String eyeType) { this.eyeType = eyeType; }
        
        public String getMouthType() { return mouthType; }
        public void setMouthType(String mouthType) { this.mouthType = mouthType; }
        
        public String getTailType() { return tailType; }
        public void setTailType(String tailType) { this.tailType = tailType; }
    }

    // 颜色方案类
    public static class ColorScheme {
        private String primaryColor = "#FF8C00"; // 主色调
        private String secondaryColor = "#FFA500"; // 辅助色
        private String eyeColor = "#0000FF"; // 眼睛颜色
        private String leftEyeColor = "#0000FF"; // 左眼颜色（支持异瞳）
        private String rightEyeColor = "#0000FF"; // 右眼颜色
        private String noseColor = "#FFB6C1"; // 鼻子颜色
        
        // Getters and Setters
        public String getPrimaryColor() { return primaryColor; }
        public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }
        
        public String getSecondaryColor() { return secondaryColor; }
        public void setSecondaryColor(String secondaryColor) { this.secondaryColor = secondaryColor; }
        
        public String getEyeColor() { return eyeColor; }
        public void setEyeColor(String eyeColor) { 
            this.eyeColor = eyeColor;
            this.leftEyeColor = eyeColor;
            this.rightEyeColor = eyeColor;
        }
        
        public String getLeftEyeColor() { return leftEyeColor; }
        public void setLeftEyeColor(String leftEyeColor) { this.leftEyeColor = leftEyeColor; }
        
        public String getRightEyeColor() { return rightEyeColor; }
        public void setRightEyeColor(String rightEyeColor) { this.rightEyeColor = rightEyeColor; }
        
        public String getNoseColor() { return noseColor; }
        public void setNoseColor(String noseColor) { this.noseColor = noseColor; }
    }

    // 图案配置类
    public static class PatternConfig {
        private String type = "none"; // none, stripes, spots, gradient, heart, scales, cheek_pouches, panda_markings, tuxedo
        private String color = "#000000"; // 图案颜色
        private String[] positions = {"body"}; // 图案位置：head, body, limbs
        private double opacity = 0.7; // 图案透明度
        private Map<String, Object> customProperties = new HashMap<>(); // 自定义属性
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        
        public String[] getPositions() { return positions; }
        public void setPositions(String[] positions) { this.positions = positions; }
        
        public double getOpacity() { return opacity; }
        public void setOpacity(double opacity) { this.opacity = opacity; }
        
        public Map<String, Object> getCustomProperties() { return customProperties; }
        public void setCustomProperties(Map<String, Object> customProperties) { this.customProperties = customProperties; }
    }

    // 装饰配置类
    public static class AccessoryConfig {
        private String hatType = "none"; // none, baseball_cap, beret, crown, bow
        private String collarType = "none"; // none, bow_tie, bell, flower_wreath, chain
        private String glassesType = "none"; // none, sunglasses, round_glasses, monocle
        private String clothingType = "none"; // none, shirt, dress, cape, sweater
        private Map<String, String> accessoryColors = new HashMap<>(); // 装饰品颜色
        
        public AccessoryConfig() {
            // 默认装饰品颜色
            accessoryColors.put("hat", "#FF0000");
            accessoryColors.put("collar", "#00FF00");
            accessoryColors.put("glasses", "#000000");
            accessoryColors.put("clothing", "#0000FF");
        }
        
        // Getters and Setters
        public String getHatType() { return hatType; }
        public void setHatType(String hatType) { this.hatType = hatType; }
        
        public String getCollarType() { return collarType; }
        public void setCollarType(String collarType) { this.collarType = collarType; }
        
        public String getGlassesType() { return glassesType; }
        public void setGlassesType(String glassesType) { this.glassesType = glassesType; }
        
        public String getClothingType() { return clothingType; }
        public void setClothingType(String clothingType) { this.clothingType = clothingType; }
        
        public Map<String, String> getAccessoryColors() { return accessoryColors; }
        public void setAccessoryColors(Map<String, String> accessoryColors) { this.accessoryColors = accessoryColors; }
    }

    // 身体比例类
    public static class BodyProportions {
        private double fatness = 50.0; // 胖瘦程度 (0-100)
        private double height = 50.0; // 高矮程度 (0-100)
        private double headSize = 50.0; // 头部大小 (0-100)
        private double eyeSize = 50.0; // 眼睛大小 (0-100)
        private double earSize = 50.0; // 耳朵大小 (0-100)
        
        // Getters and Setters
        public double getFatness() { return fatness; }
        public void setFatness(double fatness) { this.fatness = Math.max(0, Math.min(100, fatness)); }
        
        public double getHeight() { return height; }
        public void setHeight(double height) { this.height = Math.max(0, Math.min(100, height)); }
        
        public double getHeadSize() { return headSize; }
        public void setHeadSize(double headSize) { this.headSize = Math.max(0, Math.min(100, headSize)); }
        
        public double getEyeSize() { return eyeSize; }
        public void setEyeSize(double eyeSize) { this.eyeSize = Math.max(0, Math.min(100, eyeSize)); }
        
        public double getEarSize() { return earSize; }
        public void setEarSize(double earSize) { this.earSize = Math.max(0, Math.min(100, earSize)); }
    }

    // Main class getters and setters
    public BodyParts getBodyParts() { return bodyParts; }
    public void setBodyParts(BodyParts bodyParts) { this.bodyParts = bodyParts; }
    
    public ColorScheme getColorScheme() { return colorScheme; }
    public void setColorScheme(ColorScheme colorScheme) { this.colorScheme = colorScheme; }
    
    public PatternConfig getPatternConfig() { return patternConfig; }
    public void setPatternConfig(PatternConfig patternConfig) { this.patternConfig = patternConfig; }
    
    public AccessoryConfig getAccessoryConfig() { return accessoryConfig; }
    public void setAccessoryConfig(AccessoryConfig accessoryConfig) { this.accessoryConfig = accessoryConfig; }
    
    public BodyProportions getBodyProportions() { return bodyProportions; }
    public void setBodyProportions(BodyProportions bodyProportions) { this.bodyProportions = bodyProportions; }
}