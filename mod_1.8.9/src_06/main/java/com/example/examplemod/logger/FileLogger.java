package com.example.examplemod.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FileLogger {
    private static final String BASE_LOG_PATH = "C:\\temp\\MC_Mod\\";
    private static final String BASE_LOG_FILE = "error.txt";
    private static Map<String, Long> lastLogTimes = new HashMap<>();

    public static void log(String log, String fileName, long interval) {
        long currentTime = System.currentTimeMillis();
        long lastLogTime = lastLogTimes.getOrDefault(fileName, 0L);

        // 检查时间间隔
        if (currentTime - lastLogTime < interval) {
            return;
        }

        String filePath = BASE_LOG_PATH + fileName;
        File file = new File(filePath);

        // 确保目录存在
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(log + "\ttime: " + getCurrentTime() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        lastLogTimes.put(fileName, currentTime); // 更新最后记录时间
    }

    public static void log(String log, String fileName) {
        log(log, fileName, 0);
    }

    public static void log(String log) {
        log(log, BASE_LOG_FILE, 0);
    }


    private static String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return now.format(formatter);
    }
}
