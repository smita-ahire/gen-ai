package com.epam.training.gen.ai.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for generating chat completions using Azure OpenAI.
 * <p>
 * This service interacts with the Azure OpenAI API to generate chat completions
 * based on a static greeting message. It retrieves responses from the AI model
 * and logs them.
 */
@Slf4j
@Service
public class ChatService {

    @Value("${client-azureopenai-key}")
    String apiKey;
    @Value("${client-azureopenai-endpoint}")
    String endpoint;
    @Value("${client-azureopenai-deployment-name}")
    String modelId;

    public String getChat(String prompt) {
        OpenAIAsyncClient client = getOpenAIAsyncClient();
// Create your AI service client
        ChatCompletionService chatCompletionService = getChatCompletionService(client);

// Create a kernel with Azure OpenAI chat completion and plugin
        Kernel kernel= getKernel(chatCompletionService);

// Enable planning
        InvocationContext invocationContext = enablePlanning();


        ChatHistory history = new ChatHistory();
        // Add user input
        history.addUserMessage(prompt);
        // Prompt AI for response to users input
        List<ChatMessageContent<?>> results = chatCompletionService
                .getChatMessageContentsAsync(history, kernel, invocationContext)
                .block();

        return results!=null && !results.isEmpty()? results.toString():"";
    }

    private static InvocationContext enablePlanning() {
        return new InvocationContext.Builder()
                .withReturnMode(InvocationReturnMode.LAST_MESSAGE_ONLY)
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .build();
    }

    private static Kernel getKernel(ChatCompletionService chatCompletionService) {
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                //  .withPlugin(lightPlugin)
                .build();
    }

    private ChatCompletionService getChatCompletionService(OpenAIAsyncClient client) {
        return OpenAIChatCompletion.builder()
                .withModelId(modelId)
                .withOpenAIAsyncClient(client)
                .build();

    }

    private OpenAIAsyncClient getOpenAIAsyncClient() {
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(endpoint)
                .buildAsyncClient();
    }


}
