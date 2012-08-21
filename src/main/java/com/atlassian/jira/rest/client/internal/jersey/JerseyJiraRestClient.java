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

package com.atlassian.jira.rest.client.internal.jersey;

import com.atlassian.jira.rest.client.AuthenticationHandler;
import com.atlassian.jira.rest.client.ComponentRestClient;
import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.MetadataRestClient;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.SearchRestClient;
import com.atlassian.jira.rest.client.SessionRestClient;
import com.atlassian.jira.rest.client.UserRestClient;
import com.atlassian.jira.rest.client.VersionRestClient;
import com.sun.jersey.api.client.AsyncViewResource;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.ViewResource;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.ApacheHttpClientHandler;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Jersey-based implementation of JIRA REST client.
 *
 * @since v0.1
 */
public class JerseyJiraRestClient implements JiraRestClient {

    private final URI baseUri;
    private final IssueRestClient issueRestClient;
    private final SessionRestClient sessionRestClient;
	private final UserRestClient userRestClient;
	private final ProjectRestClient projectRestClient;
	private final ComponentRestClient componentRestClient;
	private final MetadataRestClient metadataRestClient;
	private final SearchRestClient searchRestClient;
	private final VersionRestClient versionRestClient;


	public JerseyJiraRestClient(final URI serverUri, final AuthenticationHandler authenticationHandler) {
        this.baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build();
        DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
        authenticationHandler.configure(config);
        final ApacheHttpClient client = new ApacheHttpClient(createDefaultClientHander(config)) {
            @Override
            public WebResource resource(URI u) {
                final WebResource resource = super.resource(u);
                authenticationHandler.configure(resource, this);
                return resource;
            }

            @Override
            public AsyncWebResource asyncResource(URI u) {
                final AsyncWebResource resource = super.asyncResource(u);
                authenticationHandler.configure(resource, this);
                return resource;
            }

            @Override
            public ViewResource viewResource(URI u) {
                final ViewResource resource = super.viewResource(u);
                authenticationHandler.configure(resource, this);
                return resource;
            }

            @Override
            public AsyncViewResource asyncViewResource(URI u) {
                final AsyncViewResource resource = super.asyncViewResource(u);
                authenticationHandler.configure(resource, this);
                return resource;
            }
        };
		metadataRestClient = new JerseyMetadataRestClient(baseUri, client);
        sessionRestClient = new JerseySessionRestClient(client, serverUri);
		issueRestClient = new JerseyIssueRestClient(baseUri, client, sessionRestClient, metadataRestClient);
		userRestClient = new JerseyUserRestClient(baseUri, client);
		projectRestClient = new JerseyProjectRestClient(baseUri, client);
		componentRestClient = new JerseyComponentRestClient(baseUri, client);
		searchRestClient = new JerseySearchRestClient(baseUri, client);
		versionRestClient = new JerseyVersionRestClient(baseUri, client);
    }

    @Override
    public IssueRestClient getIssueClient() {
        return issueRestClient;
    }

    @Override
    public SessionRestClient getSessionClient() {
        return sessionRestClient;
    }

	@Override
	public UserRestClient getUserClient() {
		return userRestClient;
	}

	@Override
	public ProjectRestClient getProjectClient() {
		return projectRestClient;
	}

	@Override
	public ComponentRestClient getComponentClient() {
		return componentRestClient;
	}

	@Override
	public MetadataRestClient getMetadataClient() {
		return metadataRestClient;
	}

	@Override
	public SearchRestClient getSearchClient() {
		return searchRestClient;
	}

	@Override
	public VersionRestClient getVersionRestClient() {
		return versionRestClient;
	}

	private static ApacheHttpClientHandler createDefaultClientHander(DefaultApacheHttpClientConfig config) {
        final HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        return new ApacheHttpClientHandler(client, config);
    }

}

