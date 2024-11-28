/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.ai.fatecloud.grpc;

import com.google.protobuf.ByteString;
import com.webank.ai.eggroll.api.networking.proxy.DataTransferServiceGrpc;
import com.webank.ai.eggroll.api.networking.proxy.Proxy;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ExchangeGrpcUtil {

    public static Proxy.Packet findExchange(String ip, int port, String key, String partyId, String operator) {


        ManagedChannel managedChannel = null;
        try {
            managedChannel = createManagedChannel(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Proxy.Topic.Builder topic = Proxy.Topic.newBuilder();
        topic.setPartyId(partyId);

        Proxy.Metadata.Builder metadata = Proxy.Metadata.newBuilder();
//        metadata.setOperator("get_route_table");
        metadata.setOperator(operator);
        metadata.setDst(topic);
        Proxy.Packet.Builder packet = Proxy.Packet.newBuilder();
        packet.setHeader(metadata);

        Proxy.Data.Builder data = Proxy.Data.newBuilder();
        long time = new Date().getTime();
        String s = DigestUtils.md5DigestAsHex((time + key).getBytes(StandardCharsets.UTF_8));
        data.setKey(s);
        byte[] bytes = (time + "").getBytes(StandardCharsets.UTF_8);
        data.setValue(ByteString.copyFrom(bytes));
        packet.setBody(data);

        Proxy.Packet build = packet.build();

        DataTransferServiceGrpc.DataTransferServiceBlockingStub dataTransferServiceBlockingStub = DataTransferServiceGrpc.newBlockingStub(managedChannel);
        Proxy.Packet packet1 = dataTransferServiceBlockingStub.unaryCall(build);
        System.out.println(packet1);

        //  shutdown channel
        shutdownManagedChannel(managedChannel);
        return packet1;
    }

    public static Proxy.Packet setExchange(String ip, int port, String key, String content, String partyId, String operator) {

        ManagedChannel managedChannel = null;
        try {
            managedChannel = createManagedChannel(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Proxy.Topic.Builder topic = Proxy.Topic.newBuilder();
        topic.setPartyId(partyId);

        Proxy.Metadata.Builder metadata = Proxy.Metadata.newBuilder();
//        metadata.setOperator("set_route_table");
        metadata.setOperator(operator);
        metadata.setDst(topic);
        Proxy.Packet.Builder packet = Proxy.Packet.newBuilder();
        packet.setHeader(metadata);

        Proxy.Data.Builder data = Proxy.Data.newBuilder();
        long time = new Date().getTime();

        String md5String = DigestUtils.md5DigestAsHex((time + content + key).getBytes(StandardCharsets.UTF_8));
        data.setKey(md5String);
        byte[] bytes = (time + content).getBytes(StandardCharsets.UTF_8);
        data.setValue(ByteString.copyFrom(bytes));
        packet.setBody(data);

        Proxy.Packet build = packet.build();

        DataTransferServiceGrpc.DataTransferServiceBlockingStub dataTransferServiceBlockingStub = DataTransferServiceGrpc
                .newBlockingStub(managedChannel).withDeadlineAfter(2, TimeUnit.SECONDS);

        Proxy.Packet unaryCall = dataTransferServiceBlockingStub.unaryCall(build);
        System.out.println(unaryCall);

        //  shutdown channel
        shutdownManagedChannel(managedChannel);
        return unaryCall;
    }

    public static ManagedChannel createManagedChannel(String ip, int port) {
        NettyChannelBuilder builder = NettyChannelBuilder
                .forAddress(ip, port)
                .keepAliveTime(9, TimeUnit.SECONDS)
                .keepAliveTimeout(9, TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .idleTimeout(9, TimeUnit.SECONDS)
                .perRpcBufferLimit(128 << 20)
                .flowControlWindow(32 << 20)
                .maxInboundMessageSize(32 << 20)
                .enableRetry()
                .retryBufferSize(16 << 20)
                .maxRetryAttempts(1);      // todo: configurable
        builder.negotiationType(NegotiationType.PLAINTEXT)
                .usePlaintext();

        return builder.build();
    }

    public static void shutdownManagedChannel(ManagedChannel managedChannel) {
        if (managedChannel == null) return;

        // request to shutdown if the channel is not shutdown
        if (!managedChannel.isShutdown()) {
            try {
                managedChannel.shutdown();
                if (managedChannel.awaitTermination(30, TimeUnit.SECONDS)) {
                    System.out.println("Timed out gracefully shutting down connection: " + managedChannel);
                }
            } catch (Exception e) {
                System.err.println("shutdown error: " + e.getMessage());
            }
        }

        // forcibly shutdown if it fails to shutdown
        if (!managedChannel.isTerminated()) {
            try {
                managedChannel.shutdownNow();
                if (managedChannel.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.out.println("Timed out gracefully shutting down now connection: " + managedChannel);
                }
            } catch (Exception e) {
                System.err.println("shutdown now error: " + e.getMessage());
            }
        }
    }
}
