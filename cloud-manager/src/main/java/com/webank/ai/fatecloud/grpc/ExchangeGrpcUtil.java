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

import com.alibaba.fastjson.JSON;
import com.google.protobuf.ByteString;
import com.webank.ai.eggroll.api.networking.proxy.DataTransferServiceGrpc;
import com.webank.ai.eggroll.api.networking.proxy.Proxy;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ExchangeGrpcUtil {

//    public static void main(String[] args) throws UnsupportedEncodingException {
//        findExchange("172.16.153.19", 9531, "eggroll","exchange","get_route_table");
//
//        HashMap<String, String> stringStringHashMap = new HashMap<>();
//        stringStringHashMap.put("1", "1");
//        String s = JSON.toJSONString(stringStringHashMap);
//        setExchange("172.16.153.14", 9531, "eggroll", s);
//
//    }

    public static Proxy.Packet findExchange(String ip, int port, String key,String partyId,String operator) throws UnsupportedEncodingException {


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
        String s = DigestUtils.md5DigestAsHex((time + key).getBytes("UTF-8"));
        data.setKey(s);
        byte[] bytes = (time + "").getBytes("UTF-8");
        data.setValue(ByteString.copyFrom(bytes));
        packet.setBody(data);

        Proxy.Packet build = packet.build();

        DataTransferServiceGrpc.DataTransferServiceBlockingStub dataTransferServiceBlockingStub = DataTransferServiceGrpc.newBlockingStub(managedChannel);
        Proxy.Packet packet1 = dataTransferServiceBlockingStub.unaryCall(build);
        System.out.println(packet1);
        return packet1;
    }

    public static Proxy.Packet setExchange(String ip, int port, String key, String content,String partyId,String operator) throws UnsupportedEncodingException {

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

        String md5String = DigestUtils.md5DigestAsHex((time + content + key).getBytes("UTF-8"));
        data.setKey(md5String);
        byte[] bytes = (time + content).getBytes("UTF-8");
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
