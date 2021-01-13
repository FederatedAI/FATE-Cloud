package com.webank.ai.fatecloud.grpc;

import com.google.protobuf.ByteString;
import com.webank.ai.eggroll.api.networking.proxy.DataTransferServiceGrpc;
import com.webank.ai.eggroll.api.networking.proxy.Proxy;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class grpcTest {

//    public void test_03_Inference() {
//
//        InferenceRequest inferenceRequest = new InferenceRequest();
//
//        inferenceRequest.setServiceId("lr-test");
//
//        inferenceRequest.getFeatureData().put("x0", 0.100016);
//        inferenceRequest.getFeatureData().put("x1", 1.210);
//        inferenceRequest.getFeatureData().put("x2", 2.321);
//        inferenceRequest.getFeatureData().put("x3", 3.432);
//        inferenceRequest.getFeatureData().put("x4", 4.543);
//        inferenceRequest.getFeatureData().put("x5", 5.654);
//        inferenceRequest.getFeatureData().put("x6", 5.654);
//        inferenceRequest.getFeatureData().put("x7", 0.102345);
//
//        inferenceRequest.getSendToRemoteFeatureData().put(Dict.DEVICE_ID, "8");
//
//        InferenceServiceProto.InferenceMessage.Builder inferenceMessageBuilder = InferenceServiceProto.InferenceMessage.newBuilder();
//        String contentString = JsonUtil.object2Json(inferenceRequest);
//        System.err.println("send data ===" + contentString);
//        try {
//            inferenceMessageBuilder.setBody(ByteString.copyFrom(contentString, "UTF-8"));
//            Map protocolMap = Maps.newHashMap();
//            protocolMap.put(Dict.PROTOCOL, Dict.RMB);
//            inferenceMessageBuilder.setHeader(ByteString.copyFrom(JsonUtil.object2Json(protocolMap), "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        InferenceServiceProto.InferenceMessage inferenceMessage = inferenceMessageBuilder.build();
//
//        System.err.println(inferenceMessage.getBody());
//
//        InferenceServiceProto.InferenceMessage resultMessage = inferenceClient.inference(inferenceMessage);
//
//        System.err.println("result ==================" + new String(resultMessage.getBody().toByteArray()));
//    }
//
//    public InferenceServiceProto.InferenceMessage inference(InferenceServiceProto.InferenceMessage inferenceMessage) {
//        ManagedChannel managedChannel = null;
//        try {
//            managedChannel = createManagedChannel(ip, port);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        InferenceServiceGrpc.InferenceServiceBlockingStub blockingStub = InferenceServiceGrpc.newBlockingStub(managedChannel);
//        InferenceServiceProto.InferenceMessage.Builder inferenceMessageBuilder =
//                InferenceServiceProto.InferenceMessage.newBuilder();
//
//        return blockingStub.inference(inferenceMessage);
//    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        findExchange("172.16.153.14",9531,"eggroll");
    }

    public static Proxy.Packet findExchange(String ip,int port,String key) throws UnsupportedEncodingException {



        ManagedChannel managedChannel = null;
        try {
            managedChannel = createManagedChannel(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Proxy.Topic.Builder topic = Proxy.Topic.newBuilder();
        topic.setPartyId("10002");

        Proxy.Metadata.Builder metadata = Proxy.Metadata.newBuilder();
        metadata.setOperator("get_route_table");
        metadata.setDst(topic);
        Proxy.Packet.Builder packet = Proxy.Packet.newBuilder();
        packet.setHeader(metadata);

        Proxy.Data.Builder data = Proxy.Data.newBuilder();
        long time = new Date().getTime();
        String s = DigestUtils.md5DigestAsHex((time + key).getBytes("UTF-8"));
        data.setKey(s);
        byte[] bytes = (time + "").getBytes("UTF-8");
        data.setValue(ByteString.copyFrom(bytes));
        packet.setBody(data);

        Proxy.Packet build = packet.build();

        DataTransferServiceGrpc.DataTransferServiceBlockingStub dataTransferServiceBlockingStub = DataTransferServiceGrpc.newBlockingStub(managedChannel);
        System.out.println(dataTransferServiceBlockingStub.unaryCall(build));
        return dataTransferServiceBlockingStub.unaryCall(build);
    }

    public static Proxy.Packet setExchange(String ip,int port,String key,String content) throws UnsupportedEncodingException {

        ManagedChannel managedChannel = null;
        try {
            managedChannel = createManagedChannel(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Proxy.Topic.Builder topic = Proxy.Topic.newBuilder();
        topic.setPartyId("10002");

        Proxy.Metadata.Builder metadata = Proxy.Metadata.newBuilder();
        metadata.setOperator("set_route_table");
        metadata.setDst(topic);
        Proxy.Packet.Builder packet = Proxy.Packet.newBuilder();
        packet.setHeader(metadata);

        Proxy.Data.Builder data = Proxy.Data.newBuilder();
        long time = new Date().getTime();

        String s = DigestUtils.md5DigestAsHex((time + key).getBytes("UTF-8"));
        data.setKey(s);
        byte[] bytes = (time + "").getBytes("UTF-8");
        data.setValue(ByteString.copyFrom(bytes));
        packet.setBody(data);

        Proxy.Packet build = packet.build();

        DataTransferServiceGrpc.DataTransferServiceBlockingStub dataTransferServiceBlockingStub = DataTransferServiceGrpc.newBlockingStub(managedChannel);
        System.out.println(dataTransferServiceBlockingStub.unaryCall(build));
        return dataTransferServiceBlockingStub.unaryCall(build);
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
