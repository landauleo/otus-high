package landau.leo.high.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import landau.leo.high.dao.DialogMessageDao;
import landau.leo.high.entity.DialogMessageEntity;
import landau.leo.high.generated.DialogMessage;
import landau.leo.high.generated.DialogueServiceGrpc;
import landau.leo.high.generated.GetUsersDialogsRequest;
import landau.leo.high.generated.GetUsersDialogsResponse;
import landau.leo.high.generated.SendUserMessageRequest;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcDialogueService extends DialogueServiceGrpc.DialogueServiceImplBase {

    private final DialogMessageDao dialogMessageDao;

    @Override
    public void getUsersDialogs(GetUsersDialogsRequest request, StreamObserver<GetUsersDialogsResponse> responseObserver) {
        List<DialogMessageEntity> list = dialogMessageDao.getMessages(request.getUserId());
        GetUsersDialogsResponse response = GetUsersDialogsResponse.newBuilder()
                .addAllMessages(list.stream()
                        .map(d -> DialogMessage.newBuilder()
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
    public void sendUserMessage(SendUserMessageRequest request, StreamObserver<Empty> responseObserver) {
        dialogMessageDao.insert(new DialogMessageEntity(UUID.fromString(request.getFromUserId()), UUID.fromString(request.getToUserId()), request.getText()));
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

}

