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

import com.atlassian.jira.rest.client.domain.IssueType;
import com.atlassian.jira.rest.client.domain.IssuelinksType;
import com.atlassian.jira.rest.client.domain.Priority;
import com.atlassian.jira.rest.client.domain.Resolution;
import com.atlassian.jira.rest.client.domain.ServerInfo;
import com.atlassian.jira.rest.client.domain.Status;

import java.net.URI;

/**
 * Serves information about JIRA metadata like server information, issue types defined, stati, priorities and resolutions.
 * This data constitutes a data dictionary which then JIRA issues base on.
 *
 * @since v0.1
 */
public interface MetadataRestClient {
	/**
	 * Retrieves from the server complete information about selected issue type
	 * @param uri URI to issue type resource (one can get it e.g. from <code>self</code> attribute
	 * of issueType field of an issue).
	 * @param progressMonitor progress monitor
	 * @return complete information about issue type resource
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	IssueType getIssueType(URI uri, ProgressMonitor progressMonitor);

	/**
	 * Retrieves from the server complete list of available issue type
	 * @param progressMonitor progress monitor
	 * @return complete information about issue type resource
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 * @since client 1.0, server 5.0
	 */
	Iterable<IssueType> getIssueTypes(ProgressMonitor progressMonitor);

    /**
     * Retrieves from the server complete list of available issue types
     * @param progressMonitor progress monitor
     * @return list of available issue types for this JIRA instance
	 * @throws RestClientException in case of problems (if linking is disabled on the server, connectivity, malformed messages, etc.)
     * @since server 4.3, client 0.5
     */
    Iterable<IssuelinksType> getIssueLinkTypes(ProgressMonitor progressMonitor);

	/**
	 * Retrieves complete information about selected status
	 * @param uri URI to this status resource (one can get it e.g. from <code>self</code> attribute
	 * of <code>status</code> field of an issue)
	 * @param progressMonitor progress monitor
	 * @return complete information about the selected status
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Status getStatus(URI uri, ProgressMonitor progressMonitor);

	/**
	 * Retrieves from the server complete information about selected priority
	 * @param uri URI for the priority resource
	 * @param progressMonitor progress monitor
	 * @return complete information about the selected priority
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Priority getPriority(URI uri, ProgressMonitor progressMonitor);

	/**
	 * Retrieves from the server complete list of available priorities
	 * @param progressMonitor progress monitor
	 * @return complete information about the selected priority
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 * @since client 1.0, server 5.0
	 */
	Iterable<Priority> getPriorities(ProgressMonitor progressMonitor);

	/**
	 * Retrieves from the server complete information about selected resolution
	 * @param uri URI for the resolution resource
	 * @param progressMonitor progress monitor
	 * @return complete information about the selected resolution
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Resolution getResolution(URI uri, ProgressMonitor progressMonitor);

	/**
	 * Retrieves from the server complete information about selected resolution
	 * @param progressMonitor progress monitor
	 * @return complete information about the selected resolution
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 * @since client 1.0, server 5.0
	 */
	Iterable<Resolution> getResolutions(ProgressMonitor progressMonitor);

	/**
	 * Retrieves information about this JIRA instance
	 * @param progressMonitor progress monitor
	 * @return information about this JIRA instance
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	ServerInfo getServerInfo(ProgressMonitor progressMonitor);
}
