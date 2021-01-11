package com.webank.ai.fatecloud.grpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;

public class grpcTest {

    public void test_03_Inference() {

        InferenceRequest inferenceRequest = new InferenceRequest();

        inferenceRequest.setServiceId("lr-test");

        inferenceRequest.getFeatureData().put("x0", 0.100016);
        inferenceRequest.getFeatureData().put("x1", 1.210);
        inferenceRequest.getFeatureData().put("x2", 2.321);
        inferenceRequest.getFeatureData().put("x3", 3.432);
        inferenceRequest.getFeatureData().put("x4", 4.543);
        inferenceRequest.getFeatureData().put("x5", 5.654);
        inferenceRequest.getFeatureData().put("x6", 5.654);
        inferenceRequest.getFeatureData().put("x7", 0.102345);

        inferenceRequest.getSendToRemoteFeatureData().put(Dict.DEVICE_ID, "8");

        InferenceServiceProto.InferenceMessage.Builder inferenceMessageBuilder = InferenceServiceProto.InferenceMessage.newBuilder();
        String contentString = JsonUtil.object2Json(inferenceRequest);
        System.err.println("send data ===" + contentString);
        try {
            inferenceMessageBuilder.setBody(ByteString.copyFrom(contentString, "UTF-8"));
            Map protocolMap = Maps.newHashMap();
            protocolMap.put(Dict.PROTOCOL, Dict.RMB);
            inferenceMessageBuilder.setHeader(ByteString.copyFrom(JsonUtil.object2Json(protocolMap), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        InferenceServiceProto.InferenceMessage inferenceMessage = inferenceMessageBuilder.build();

        System.err.println(inferenceMessage.getBody());

        InferenceServiceProto.InferenceMessage resultMessage = inferenceClient.inference(inferenceMessage);

        System.err.println("result ==================" + new String(resultMessage.getBody().toByteArray()));
    }

    public InferenceServiceProto.InferenceMessage inference(InferenceServiceProto.InferenceMessage inferenceMessage) {
        ManagedChannel managedChannel = null;
        try {
            managedChannel = createManagedChannel(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        InferenceServiceGrpc.InferenceServiceBlockingStub blockingStub = InferenceServiceGrpc.newBlockingStub(managedChannel);
        InferenceServiceProto.InferenceMessage.Builder inferenceMessageBuilder =
                InferenceServiceProto.InferenceMessage.newBuilder();

        return blockingStub.inference(inferenceMessage);
    }

    public static ManagedChannel createManagedChannel(String ip, int port) throws Exception {
        NettyChannelBuilder builder = NettyChannelBuilder
                .forAddress(ip, port)
                .keepAliveTime(60, TimeUnit.SECONDS)
                .keepAliveTimeout(60, TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .idleTimeout(60, TimeUnit.SECONDS)
                .perRpcBufferLimit(128 << 20)
                .flowControlWindow(32 << 20)
                .maxInboundMessageSize(32 << 20)
                .enableRetry()
                .retryBufferSize(16 << 20)
                .maxRetryAttempts(20);      // todo: configurable
        builder.negotiationType(NegotiationType.PLAINTEXT)
                .usePlaintext();

        return builder.build();
    }
}
