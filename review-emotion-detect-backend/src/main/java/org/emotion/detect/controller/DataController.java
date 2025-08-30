package org.emotion.detect.controller;

import org.emotion.detect.entity.EmotionCategory;
import org.emotion.detect.vo.ResponseVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DataController {

    @PostMapping("/emotion_analysis")
    public ResponseVo<List<EmotionCategory>> getEmotionAnalysis(@RequestBody List<String> emotionTypes) {
        List<EmotionCategory> emotions = new ArrayList<>();

        for (String emotionType : emotionTypes) {
            switch (emotionType.toLowerCase()) {
                case "happy":
                    emotions.add(new EmotionCategory("happy", "Feeling happy brings a wide range of positive effects on the mind, body, and social life. Psychologically, happiness boosts confidence, creativity, and motivation while reducing stress and anxiety. Physically, it supports immune function, protects heart health, improves sleep, and even reduces pain through the release of endorphins. Socially, happiness enhances communication, strengthens relationships, and creates a positive atmosphere that encourages collaboration." +
                            "\n" +
                            "In short, happiness is not just a pleasant emotion but a powerful force that fosters well-being, resilience, and stronger connections with others, creating a positive cycle for both personal growth and social harmony."));
                    break;
                case "sad":
                    emotions.add(new EmotionCategory("sad", "Sadness, though often seen as negative, also has significant impacts on the mind, body, and relationships. Psychologically, sadness can lower motivation, reduce concentration, and increase stress or anxiety, sometimes leading to feelings of hopelessness if prolonged. Physically, it may cause fatigue, sleep disturbances, weakened immunity, or changes in appetite. Socially, sadness can make people withdraw from others, reduce communication, and sometimes strain relationships." +
                            "\n" +
                            "However, sadness also has a constructive side: it helps people reflect more deeply, process loss, and develop empathy and resilience. In short, while sadness may feel heavy, it plays an important role in emotional balance and personal growth, reminding us of our needs and guiding us toward healing."));
                    break;
                case "bad":
                    emotions.add(new EmotionCategory("bad", "Feeling bad or negative emotions can have various impacts on our well-being. Psychologically, negative emotions can cloud judgment, reduce motivation, and create a pessimistic outlook. Physically, prolonged negative emotions may lead to stress-related symptoms, weakened immune system, and sleep disturbances. Socially, negative emotions can affect relationships and communication with others." +
                            "\n" +
                            "However, recognizing and processing these emotions is important for emotional health. It's normal to experience negative emotions, and they can serve as signals that something needs attention or change in our lives."));
                    break;
                default:
                    emotions.add(new EmotionCategory(emotionType, "Analysis not available for this emotion type."));
                    break;
            }
        }
        
        return ResponseVo.success(emotions);
    }
}



