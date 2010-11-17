/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.processor;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorChain;
import org.mule.api.processor.policy.Policies;
import org.mule.routing.WireTap;
import org.mule.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class NullMessageProcessor implements MessageProcessorChain
{
    public MuleEvent process(MuleEvent event) throws MuleException
    {
        return event;
    }

    @Override
    public String toString()
    {
        return ObjectUtils.toString(this);
    }

    public List<MessageProcessor> getMessageProcessors()
    {
        return Collections.emptyList();
    }

    public String getName()
    {
        return null;
    }

    public Policies getPolicies()
    {
        return new Policies(this);
    }

    public Map<MessageProcessor, WireTap> getCallbackMap()
    {
        return null;
    }
}
