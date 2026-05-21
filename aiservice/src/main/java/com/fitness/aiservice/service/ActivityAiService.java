package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getRecommendations(prompt);
        log.info("RESPONSE FROM AI {}", aiResponse);
        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {

        try {

            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            log.info("========== CLEANED AI RESPONSE ==========");
            log.info(jsonContent);
            log.info("=========================================");

        } catch (Exception e) {

            log.error("Error processing AI response", e);
        }

        return null;
    }

    private String createPromptForActivity(Activity activity) {

        return String.format("""
            
            You are an expert AI fitness coach.

            Analyze the following fitness activity and provide a detailed response 
            in the EXACT JSON format shown below.

            IMPORTANT RULES:
            1. Response must be valid JSON only.
            2. Do not add markdown formatting.
            3. Do not add extra explanations outside JSON.
            4. Keep recommendations practical and detailed.

            REQUIRED JSON FORMAT:

            {
              "analysis": {
                "overall": "Overall performance analysis",
                "pace": "Pace analysis",
                "heartRate": "Heart rate analysis",
                "caloriesBurned": "Calories burned analysis"
              },

              "improvements": [
                {
                  "area": "Improvement area",
                  "recommendation": "Detailed recommendation"
                }
              ],

              "suggestions": [
                {
                  "workout": "Suggested workout name",
                  "description": "Workout description"
                }
              ],

              "safety": [
                "Safety recommendation 1",
                "Safety recommendation 2"
              ]
            }

            FITNESS ACTIVITY DETAILS:

            Activity Type: %s
            Duration: %d minutes
            Calories Burned: %d
            Additional Metrics: %s

            Focus your analysis on:
            - Performance evaluation
            - Endurance and recovery
            - Training improvements
            - Workout recommendations
            - Injury prevention
            - Safety guidance

            """,

                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
