/*
 * Copyright 2018 NAVER Corp.
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

package com.navercorp.pinpoint.collector.receiver.thrift;

import com.navercorp.pinpoint.collector.handler.thrift.ThriftAgentEventHandler;
import com.navercorp.pinpoint.collector.handler.thrift.ThriftAgentStatHandlerV2;
import com.navercorp.pinpoint.collector.handler.SimpleHandler;
import com.navercorp.pinpoint.collector.handler.SimpleDualHandler;
import com.navercorp.pinpoint.io.header.Header;
import com.navercorp.pinpoint.io.request.ServerRequest;
import com.navercorp.pinpoint.io.request.ServerResponse;
import com.navercorp.pinpoint.thrift.io.DefaultTBaseLocator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author emeroad
 * @author hyungil.jeong
 */
public class StatDispatchHandler implements DispatchHandler {

    @Autowired
    private ThriftAgentStatHandlerV2 thriftAgentStatHandler;

    @Autowired
    private ThriftAgentEventHandler thriftAgentEventHandler;


    public StatDispatchHandler() {

    }

    private SimpleHandler getSimpleHandler(Header header) {
        // To change below code to switch table make it a little bit faster.
        // FIXME (2014.08) Legacy - TAgentStats should not be sent over the wire.
        final short type = header.getType();
        if (type == DefaultTBaseLocator.AGENT_STAT || type == DefaultTBaseLocator.AGENT_STAT_BATCH) {
            return new SimpleDualHandler(thriftAgentStatHandler, thriftAgentEventHandler);
        }

        throw new UnsupportedOperationException("unsupported header:" + header);
    }

    @Override
    public void dispatchSendMessage(ServerRequest serverRequest) {
        SimpleHandler simpleHandler = getSimpleHandler(serverRequest.getHeader());
        simpleHandler.handleSimple(serverRequest);
    }

    @Override
    public void dispatchRequestMessage(ServerRequest serverRequest, ServerResponse serverResponse) {

    }


}
