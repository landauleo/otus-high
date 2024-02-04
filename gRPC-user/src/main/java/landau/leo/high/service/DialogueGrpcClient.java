package landau.leo.high.service;

import java.util.List;
import java.util.UUID;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import landau.leo.high.generated.DialogueServiceGrpc;
import landau.leo.high.generated.DialogueServiceOuterClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DialogueGrpcClient {

    private final ManagedChannel channel;
    private final DialogueServiceGrpc.DialogueServiceBlockingStub dialogueServiceStub;

    public DialogueGrpcClient() {
        this.channel = ManagedChannelBuilder.forAddress("localhost", 8090)
                .usePlaintext()
                .build();
        this.dialogueServiceStub = DialogueServiceGrpc.newBlockingStub(channel);
    }


    public List<DialogueServiceOuterClass.DialogMessage> getUsersDialogs(String userId) {
        DialogueServiceOuterClass.GetUsersDialogsRequest request = DialogueServiceOuterClass.GetUsersDialogsRequest.newBuilder().setUserId(userId).build();
        DialogueServiceOuterClass.GetUsersDialogsResponse response = dialogueServiceStub.getUsersDialogs(request);
        return response.getMessagesList(); // Преобразовать ответ в нужный формат, если требуется
    }

    public void sendUserMessage(UUID fromUserId, UUID toUserId, String text) {
        DialogueServiceOuterClass.SendUserMessageRequest request = DialogueServiceOuterClass.SendUserMessageRequest.newBuilder()
                .setFromUserId(fromUserId.toString())
                .setToUserId(toUserId.toString())
                .setText(text)
                .build();
        dialogueServiceStub.sendUserMessage(request);
    }

    // Добавьте метод для закрытия канала при уничтожении бина
    @PreDestroy
    public void shutdown() {
        channel.shutdown();
    }

}
