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

import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.internal.json.BasicProjectsJsonParser;
import com.atlassian.jira.rest.client.internal.json.JsonParser;
import com.atlassian.jira.rest.client.internal.json.ProjectJsonParser;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.concurrent.Callable;

/**
 * Jersey-based implementation of ProjectRestClient
 *
 * @since v0.1
 */
public class JerseyProjectRestClient extends AbstractJerseyRestClient implements ProjectRestClient {
	private static final String PROJECT_URI_PREFIX = "project";
	private final ProjectJsonParser projectJsonParser = new ProjectJsonParser();
	private final BasicProjectsJsonParser basicProjectsJsonParser = new BasicProjectsJsonParser();

	public JerseyProjectRestClient(URI baseUri, ApacheHttpClient client) {
		super(baseUri, client);
	}

	@Override
	public Project getProject(final URI projectUri, ProgressMonitor progressMonitor) {
		return invoke(new Callable<Project>() {
			@Override
			public Project call() throws Exception {
				final WebResource projectResource = client.resource(projectUri);
				final JSONObject jsonObject = projectResource.get(JSONObject.class);
				return projectJsonParser.parse(jsonObject);
			}
		});

	}

	@Override
	public Project getProject(final String key, ProgressMonitor progressMonitor) {
		final URI uri = UriBuilder.fromUri(baseUri).path(PROJECT_URI_PREFIX).path(key).build();
		return getProject(uri, progressMonitor);
	}

	@Override
	public Iterable<BasicProject> getAllProjects(ProgressMonitor progressMonitor) {
		final URI uri = UriBuilder.fromUri(baseUri).path(PROJECT_URI_PREFIX).build();
		return getAndParse(uri, basicProjectsJsonParser, progressMonitor);
	}
}
