package org.emotion.detect.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @RequestMapping("/")
    String hello() {
        return "Hello World!";
    }
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/test/db-connection")
    public Map<String, Object> testDatabaseConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 测试基本连接
            String message = jdbcTemplate.queryForObject("SELECT 'Database connection successful!' as message", String.class);
            result.put("status", "success");
            result.put("message", message);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Database connection failed: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 获取数据库表信息
     */
    @GetMapping("/test/db-tables")
    public Map<String, Object> getDatabaseTables() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取所有表名
            List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'moodlens'",
                String.class
            );
            
            result.put("status", "success");
            result.put("tables", tables);
            result.put("tableCount", tables.size());
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Failed to get tables: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 获取情感分类数据
     */
    @GetMapping("/test/emotion-categories")
    public Map<String, Object> getEmotionCategories() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取情感分类列表
            List<Map<String, Object>> categories = jdbcTemplate.queryForList(
                "SELECT ekman_id, name FROM ekman_category ORDER BY ekman_id"
            );
            
            result.put("status", "success");
            result.put("categories", categories);
            result.put("categoryCount", categories.size());
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Failed to get emotion categories: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 获取研究句子数据
     */
    @GetMapping("/test/study-sentences")
    public Map<String, Object> getStudySentences() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取研究句子数据（限制前10条）
            List<Map<String, Object>> sentences = jdbcTemplate.queryForList(
                "SELECT sentence_id, text, ekman_id FROM study_sentence LIMIT 10"
            );
            
            result.put("status", "success");
            result.put("sentences", sentences);
            result.put("sentenceCount", sentences.size());
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Failed to get study sentences: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 获取数据库统计信息
     */
    @GetMapping("/test/db-stats")
    public Map<String, Object> getDatabaseStats() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取各表的记录数
            String[] tables = {"ekman_category", "study_sentence", "reference_item", "sentence_reference", "category_reference", "goemotion_label"};
            
            for (String table : tables) {
                try {
                    Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
                    stats.put(table + "_count", count);
                } catch (Exception e) {
                    stats.put(table + "_count", "error: " + e.getMessage());
                }
            }
            
            result.put("status", "success");
            result.put("statistics", stats);
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Failed to get database stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}
