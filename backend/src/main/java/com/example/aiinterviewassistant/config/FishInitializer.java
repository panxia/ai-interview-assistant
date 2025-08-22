package com.example.aiinterviewassistant.config;

import com.example.aiinterviewassistant.service.FishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FishInitializer implements CommandLineRunner {
    
    private final FishService fishService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("初始化示例鱼群...");
        fishService.initializeSampleFishes();
        log.info("示例鱼群初始化完成！");
    }
}
