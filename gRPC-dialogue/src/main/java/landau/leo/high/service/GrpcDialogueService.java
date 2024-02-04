package landau.leo.high.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import landau.leo.high.dao.DialogMessageDao;
import landau.leo.high.entity.DialogMessageEntity;
import landau.leo.high.generated.DialogueServiceGrpc;
import landau.leo.high.generated.DialogueServiceOuterClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcDialogueService extends DialogueServiceGrpc.DialogueServiceImplBase {

    private final DialogMessageDao dialogMessageDao;

    @Override
    public void getUsersDialogs(DialogueServiceOuterClass.GetUsersDialogsRequest request, StreamObserver<DialogueServiceOuterClass.GetUsersDialogsResponse> responseObserver) {
        List<DialogMessageEntity> list = dialogMessageDao.getMessages(request.getUserId());
        DialogueServiceOuterClass.GetUsersDialogsResponse response = DialogueServiceOuterClass.GetUsersDialogsResponse.newBuilder()
                .addAllMessages(list.stream()
                        .map(d -> DialogueServiceOuterClass.DialogMessage.newBuilder()
                                .setFromUserId(d.getFrom().toString())
                                .setToUserId(d.getTo().toString())
                                .setText(d.getText())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void sendUserMessage(DialogueServiceOuterClass.SendUserMessageRequest request, StreamObserver<Empty> responseObserver) {
        dialogMessageDao.insert(new DialogMessageEntity(UUID.fromString(request.getFromUserId()), UUID.fromString(request.getToUserId()), request.getText()));
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

}

