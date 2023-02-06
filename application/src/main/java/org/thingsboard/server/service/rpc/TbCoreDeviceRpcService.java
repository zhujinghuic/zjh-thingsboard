/**
 * Copyright © 2016-2022 The Thingsboard Authors
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
package org.thingsboard.server.service.rpc;

import org.thingsboard.server.common.msg.rpc.FromDeviceRpcResponse;
import org.thingsboard.server.common.msg.rpc.ToDeviceRpcRequest;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.function.Consumer;

/**
 * Handles REST API calls that contain RPC requests to Device.
 */
public interface TbCoreDeviceRpcService {

    /**
     * 处理包含对设备的RPC请求的REST API调用，并将其推送到规则引擎。
     * Handles REST API calls that contain RPC requests to Device and pushes them to Rule Engine.
     * Schedules the timeout for the RPC call based on the {@link ToDeviceRpcRequest}
     *  @param request          the RPC REQUEST
     * @param responseConsumer the consumer of the RPC response
     * @param currentUser
     */
    void processRestApiRpcRequest(ToDeviceRpcRequest request, Consumer<FromDeviceRpcResponse> responseConsumer, SecurityUser currentUser);

    /**
     * 处理来自规则引擎的RPC响应。
     * Handles the RPC response from the Rule Engine.
     *
     * @param response the RPC response
     */
    void processRpcResponseFromRuleEngine(FromDeviceRpcResponse response);

    /**
     * 将RPC请求从规则引擎转发到设备Actor
     * Forwards the RPC request from Rule Engine to Device Actor
     *
     * @param request the RPC request message
     */
    void forwardRpcRequestToDeviceActor(ToDeviceRpcRequestActorMsg request);

    /**
     * 处理来自设备参与者（传输）的RPC响应。
     * Handles the RPC response from the Device Actor (Transport).
     *
     * @param response the RPC response
     */
    void processRpcResponseFromDeviceActor(FromDeviceRpcResponse response);

    void processRemoveRpc(RemoveRpcActorMsg removeRpcMsg);

}
