package com.scd.mvctest.config;

/*
 *
 *  Copyright 2017-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerCombiner;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.spi.service.contexts.Orderings.*;

class DefaultRequestHandlerCombiner implements RequestHandlerCombiner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRequestHandlerCombiner.class);

    @Override
    public List<RequestHandler> combine(List<RequestHandler> source) {
        List<RequestHandler> combined = new ArrayList<RequestHandler>();
        Multimap<String, RequestHandler> byPath = LinkedListMultimap.create();
        LOGGER.debug("Total number of request handlers {}", nullToEmptyList(source).size());
        for (RequestHandler each : nullToEmptyList(source)) {
            LOGGER.debug("Adding key: {}, {}", patternsCondition(each).toString(), each.toString());
            byPath.put(patternsCondition(each).toString(), each);
        }
        for (String key : byPath.keySet()) {
            combined.addAll(byPath.get(key));
        }
        LOGGER.debug("Combined number of request handlers {}", combined.size());
        return byPatternsCondition().sortedCopy(combined);
    }
}
