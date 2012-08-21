/*
 * Copyright (C) 2010 Atlassian
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

package com.atlassian.jira.rest.client.auth;

import com.atlassian.jira.rest.client.AuthenticationHandler;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.Filterable;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;

/**
 * TODO: Document this class / interface here
 * This class is still under construction. Do not use.
 *
 * @since v0.1
 */
public class SessionAuthenticationHandler implements AuthenticationHandler {
    private final String username;
    private final String password;

    public SessionAuthenticationHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void configure(ApacheHttpClientConfig apacheHttpClientConfig) {
//        apacheHttpClientConfig.getState().getHttpState().addCookie();
    }

    @Override
    public void configure(Filterable filterable, Client client) {
//		webResource.cookie(new Comp)
//        client.
    }
}
