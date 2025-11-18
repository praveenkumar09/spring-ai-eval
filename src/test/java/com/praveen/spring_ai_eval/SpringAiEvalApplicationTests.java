package com.praveen.spring_ai_eval;

import com.praveen.spring_ai_eval.controller.ChatController;
import org.junit.jupiter.api.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.Assert;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "spring.ai.openai.api-key=${OPENAI_API_KEY:test-key}",
        "logging.level.org.springframework.ai=DEBUG"
        })
class SpringAiEvalApplicationTests {

    @Autowired
    private ChatController chatController;

    @Autowired
    private ChatModel chatModel;

    private ChatClient chatClient;

    private RelevancyEvaluator relevancyEvaluator;

    private final float minRelevancyScore = 0.7F;

    @BeforeEach
    void setup() {
        ChatClient.Builder builder = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor());
        this.chatClient = builder.build();
        this.relevancyEvaluator = new RelevancyEvaluator(builder);
    }

	@Test
    @DisplayName("Should return relevant response for basic geography question")
    @Timeout(30)
	void evaluateChatControllerResponseRelevancy(){
        //Given
        String question = "What is the capital of India?";

        //When
        String response = chatController.chat(question);

        EvaluationRequest evaluationRequest = new EvaluationRequest(question,response);
        EvaluationResponse evaluateResponse = relevancyEvaluator.evaluate(evaluationRequest);

        Assertions.assertAll(
                () -> assertThat(response).isNotBlank(),
                () -> assertThat(evaluateResponse.isPass())
                .withFailMessage("Evaluation failed for question: " + question)
                .isTrue(),
                () -> assertThat(evaluateResponse.getScore())
                .withFailMessage("Evaluation score for question: " + question + " is less than " + minRelevancyScore)
                        .isGreaterThan(minRelevancyScore)
        );

    }

}
