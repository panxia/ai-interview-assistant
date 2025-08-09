package com.example.aiinterviewassistant.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 宠物外观自定义系统
 * 支持深度外观定制：身体部位、颜色、图案、装饰
 */
public class PetAppearance {
    
    // 身体部位配置
    private HeadShape headShape = HeadShape.ROUND;
    private EarStyle earStyle = EarStyle.POINTED;
    private EyeType eyeType = EyeType.NORMAL;
    private MouthExpression mouthExpression = MouthExpression.SMILE;
    private BodyProportion bodyProportion = new BodyProportion();
    
    // 颜色系统
    private String primaryColor = "#FFA500"; // 主色调
    private String secondaryColor = "#FFFFFF"; // 辅助色
    private String eyeColorLeft = "#4169E1"; // 左眼颜色
    private String eyeColorRight = "#4169E1"; // 右眼颜色（支持异瞳）
    private String noseColor = "#FFB6C1"; // 鼻子颜色
    
    // 图案系统
    private Pattern pattern = Pattern.NONE;
    private String patternColor = "#000000";
    private PatternPosition patternPosition = PatternPosition.BODY;
    
    // 装饰系统
    private Accessory hat = Accessory.NONE;
    private Accessory collar = Accessory.NONE;
    private Accessory glasses = Accessory.NONE;
    private Map<String, Accessory> customAccessories = new HashMap<>();
    
    // 特殊效果
    private boolean hasGlow = false;
    private String glowColor = "#FFFF00";
    private boolean hasSparkles = false;
    
    // 枚举定义
    public enum HeadShape {
        ROUND("圆形", "round"),
        OVAL("椭圆形", "oval"),
        SQUARE("方形", "square"),
        HEART("心形", "heart");
        
        private final String displayName;
        private final String code;
        
        HeadShape(String displayName, String code) {
            this.displayName = displayName;
            this.code = code;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
    }
    
    public enum EarStyle {
        POINTED("尖耳", "pointed"),
        ROUND("圆耳", "round"),
        DROOPY("垂耳", "droopy"),
        NONE("无耳", "none"),
        BUNNY("兔耳", "bunny");
        
        private final String displayName;
        private final String code;
        
        EarStyle(String displayName, String code) {
            this.displayName = displayName;
            this.code = code;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
    }
    
    public enum EyeType {
        NORMAL("普通眼", "normal"),
        BIG("大眼", "big"),
        SMALL("小眼", "small"),
        SLEEPY("眯眯眼", "sleepy"),
        STAR("星星眼", "star"),
        HEART("爱心眼", "heart");
        
        private final String displayName;
        private final String code;
        
        EyeType(String displayName, String code) {
            this.displayName = displayName;
            this.code = code;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
    }
    
    public enum MouthExpression {
        SMILE("微笑", "smile"),
        POUT("噘嘴", "pout"),
        OPEN("张嘴", "open"),
        CLOSED("闭嘴", "closed"),
        TONGUE("吐舌", "tongue");
        
        private final String displayName;
        private final String code;
        
        MouthExpression(String displayName, String code) {
            this.displayName = displayName;
            this.code = code;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
    }
    
    public enum Pattern {
        NONE("无图案", "none"),
        STRIPES("条纹", "stripes"),
        SPOTS("斑点", "spots"),
        GRADIENT("渐变", "gradient"),
        HEARTS("心形", "hearts"),
        STARS("星星", "stars"),
        FLOWERS("花朵", "flowers");
        
        private final String displayName;
        private final String code;
        
        Pattern(String displayName, String code) {
            this.displayName = displayName;
            this.code = code;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
    }
    
    public enum PatternPosition {
        HEAD("头部", "head"),
        BODY("身体", "body"),
        LIMBS("四肢", "limbs"),
        ALL("全身", "all");
        
        private final String displayName;
        private final String code;
        
        PatternPosition(String displayName, String code) {
            this.displayName = displayName;
            this.code = code;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
    }
    
    public enum Accessory {
        NONE("无", "none", 0),
        // 帽子类
        BASEBALL_CAP("棒球帽", "baseball_cap", 50),
        BERET("贝雷帽", "beret", 60),
        CROWN("王冠", "crown", 200),
        WIZARD_HAT("巫师帽", "wizard_hat", 150),
        SANTA_HAT("圣诞帽", "santa_hat", 100),
        // 项圈类
        BOW_TIE("蝴蝶结", "bow_tie", 30),
        BELL_COLLAR("铃铛项圈", "bell_collar", 40),
        FLOWER_WREATH("花环", "flower_wreath", 80),
        SCARF("围巾", "scarf", 50),
        // 眼镜类
        SUNGLASSES("墨镜", "sunglasses", 70),
        ROUND_GLASSES("圆框镜", "round_glasses", 60),
        MONOCLE("单片镜", "monocle", 90),
        STAR_GLASSES("星星眼镜", "star_glasses", 100);
        
        private final String displayName;
        private final String code;
        private final int price;
        
        Accessory(String displayName, String code, int price) {
            this.displayName = displayName;
            this.code = code;
            this.price = price;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
        public int getPrice() { return price; }
    }
    
    /**
     * 身体比例类
     */
    public static class BodyProportion {
        private float fatness = 0.5f; // 胖瘦度 0-1
        private float height = 0.5f; // 高矮度 0-1
        private float headSize = 0.5f; // 头部大小 0-1
        private float limbLength = 0.5f; // 四肢长度 0-1
        private float tailLength = 0.5f; // 尾巴长度 0-1
        
        // Getters and Setters
        public float getFatness() { return fatness; }
        public void setFatness(float fatness) { 
            this.fatness = Math.max(0, Math.min(1, fatness)); 
        }
        
        public float getHeight() { return height; }
        public void setHeight(float height) { 
            this.height = Math.max(0, Math.min(1, height)); 
        }
        
        public float getHeadSize() { return headSize; }
        public void setHeadSize(float headSize) { 
            this.headSize = Math.max(0, Math.min(1, headSize)); 
        }
        
        public float getLimbLength() { return limbLength; }
        public void setLimbLength(float limbLength) { 
            this.limbLength = Math.max(0, Math.min(1, limbLength)); 
        }
        
        public float getTailLength() { return tailLength; }
        public void setTailLength(float tailLength) { 
            this.tailLength = Math.max(0, Math.min(1, tailLength)); 
        }
    }
    
    // 构造函数
    public PetAppearance() {
        // 默认外观
    }
    
    /**
     * 应用预设主题
     */
    public void applyTheme(String theme) {
        switch (theme) {
            case "cute" -> {
                this.headShape = HeadShape.ROUND;
                this.eyeType = EyeType.BIG;
                this.primaryColor = "#FFB6C1";
                this.pattern = Pattern.HEARTS;
            }
            case "cool" -> {
                this.headShape = HeadShape.SQUARE;
                this.eyeType = EyeType.SLEEPY;
                this.primaryColor = "#4169E1";
                this.glasses = Accessory.SUNGLASSES;
            }
            case "elegant" -> {
                this.headShape = HeadShape.OVAL;
                this.eyeType = EyeType.NORMAL;
                this.primaryColor = "#9370DB";
                this.collar = Accessory.BOW_TIE;
            }
            case "wild" -> {
                this.earStyle = EarStyle.POINTED;
                this.pattern = Pattern.STRIPES;
                this.primaryColor = "#FF8C00";
            }
        }
    }
    
    /**
     * 获取外观描述
     */
    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append("一只").append(headShape.getDisplayName()).append("脸的宠物，");
        desc.append("有着").append(earStyle.getDisplayName()).append("和");
        desc.append(eyeType.getDisplayName()).append("，");
        
        if (pattern != Pattern.NONE) {
            desc.append("身上有").append(pattern.getDisplayName()).append("图案，");
        }
        
        if (hat != Accessory.NONE) {
            desc.append("戴着").append(hat.getDisplayName()).append("，");
        }
        
        if (collar != Accessory.NONE) {
            desc.append("佩戴着").append(collar.getDisplayName()).append("，");
        }
        
        if (glasses != Accessory.NONE) {
            desc.append("戴着").append(glasses.getDisplayName()).append("，");
        }
        
        desc.append("看起来非常可爱！");
        
        return desc.toString();
    }
    
    // Getters and Setters
    public HeadShape getHeadShape() { return headShape; }
    public void setHeadShape(HeadShape headShape) { this.headShape = headShape; }
    
    public EarStyle getEarStyle() { return earStyle; }
    public void setEarStyle(EarStyle earStyle) { this.earStyle = earStyle; }
    
    public EyeType getEyeType() { return eyeType; }
    public void setEyeType(EyeType eyeType) { this.eyeType = eyeType; }
    
    public MouthExpression getMouthExpression() { return mouthExpression; }
    public void setMouthExpression(MouthExpression mouthExpression) { this.mouthExpression = mouthExpression; }
    
    public BodyProportion getBodyProportion() { return bodyProportion; }
    public void setBodyProportion(BodyProportion bodyProportion) { this.bodyProportion = bodyProportion; }
    
    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }
    
    public String getSecondaryColor() { return secondaryColor; }
    public void setSecondaryColor(String secondaryColor) { this.secondaryColor = secondaryColor; }
    
    public String getEyeColorLeft() { return eyeColorLeft; }
    public void setEyeColorLeft(String eyeColorLeft) { this.eyeColorLeft = eyeColorLeft; }
    
    public String getEyeColorRight() { return eyeColorRight; }
    public void setEyeColorRight(String eyeColorRight) { this.eyeColorRight = eyeColorRight; }
    
    public String getNoseColor() { return noseColor; }
    public void setNoseColor(String noseColor) { this.noseColor = noseColor; }
    
    public Pattern getPattern() { return pattern; }
    public void setPattern(Pattern pattern) { this.pattern = pattern; }
    
    public String getPatternColor() { return patternColor; }
    public void setPatternColor(String patternColor) { this.patternColor = patternColor; }
    
    public PatternPosition getPatternPosition() { return patternPosition; }
    public void setPatternPosition(PatternPosition patternPosition) { this.patternPosition = patternPosition; }
    
    public Accessory getHat() { return hat; }
    public void setHat(Accessory hat) { this.hat = hat; }
    
    public Accessory getCollar() { return collar; }
    public void setCollar(Accessory collar) { this.collar = collar; }
    
    public Accessory getGlasses() { return glasses; }
    public void setGlasses(Accessory glasses) { this.glasses = glasses; }
    
    public Map<String, Accessory> getCustomAccessories() { return customAccessories; }
    public void setCustomAccessories(Map<String, Accessory> customAccessories) { 
        this.customAccessories = customAccessories; 
    }
    
    public boolean isHasGlow() { return hasGlow; }
    public void setHasGlow(boolean hasGlow) { this.hasGlow = hasGlow; }
    
    public String getGlowColor() { return glowColor; }
    public void setGlowColor(String glowColor) { this.glowColor = glowColor; }
    
    public boolean isHasSparkles() { return hasSparkles; }
    public void setHasSparkles(boolean hasSparkles) { this.hasSparkles = hasSparkles; }
}