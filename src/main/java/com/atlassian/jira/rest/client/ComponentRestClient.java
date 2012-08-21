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

package com.atlassian.jira.rest.client;

import com.atlassian.jira.rest.client.domain.Component;
import com.atlassian.jira.rest.client.domain.input.ComponentInput;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * The client handling component resources
 *
 * @since v0.1
 */
public interface ComponentRestClient {
	/**
	 * @param componentUri URI to selected component resource
	 * @param progressMonitor progress monitor
	 * @return complete information about selected component
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Component getComponent(URI componentUri, ProgressMonitor progressMonitor);

	Component createComponent(String projectKey, ComponentInput componentInput, ProgressMonitor progressMonitor);

	Component updateComponent(URI componentUri, ComponentInput componentInput, ProgressMonitor progressMonitor);

	void removeComponent(URI componentUri, @Nullable URI moveIssueToComponentUri, ProgressMonitor progressMonitor);

	int getComponentRelatedIssuesCount(URI componentUri, ProgressMonitor progressMonitor);
}
