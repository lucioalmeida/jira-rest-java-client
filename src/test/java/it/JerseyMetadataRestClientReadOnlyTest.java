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

package it;

import com.atlassian.jira.nimblefunctests.annotation.RestoreOnce;
import com.atlassian.jira.rest.client.TestUtil;
import com.atlassian.jira.rest.client.domain.BasicIssueType;
import com.atlassian.jira.rest.client.domain.BasicPriority;
import com.atlassian.jira.rest.client.domain.BasicResolution;
import com.atlassian.jira.rest.client.domain.BasicStatus;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.IssueType;
import com.atlassian.jira.rest.client.domain.IssuelinksType;
import com.atlassian.jira.rest.client.domain.Priority;
import com.atlassian.jira.rest.client.domain.Resolution;
import com.atlassian.jira.rest.client.domain.ServerInfo;
import com.atlassian.jira.rest.client.domain.Status;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.json.TestConstants;
import com.google.common.collect.Iterables;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

/**
 * Those tests mustn't change anything on server side, as jira is restored only once
 */
@RestoreOnce(TestConstants.DEFAULT_JIRA_DUMP_FILE)
public class JerseyMetadataRestClientReadOnlyTest extends AbstractJerseyRestClientTest {
	@Test
	public void testGetServerInfo() throws Exception {
		final ServerInfo serverInfo = client.getMetadataClient().getServerInfo(pm);
		assertEquals("Your Company JIRA", serverInfo.getServerTitle());
		assertTrue(serverInfo.getBuildDate().isBeforeNow());
		assertTrue(serverInfo.getServerTime().isAfter(new DateTime().minusMinutes(5)));
		assertTrue(serverInfo.getServerTime().isBefore(new DateTime().plusMinutes(5)));
	}

	@Test
	public void testGetIssueTypeNonExisting() throws Exception {
		final BasicIssueType basicIssueType = client.getIssueClient().getIssue("TST-1", pm).getIssueType();
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND, "The issue type with id '" +
				TestUtil.getLastPathSegment(basicIssueType.getSelf()) + "fake" +
				"' does not exist", new Runnable() {
			@Override
			public void run() {
				client.getMetadataClient().getIssueType(TestUtil.toUri(basicIssueType.getSelf() + "fake"), pm);
			}
		});
	}

	@Test
	public void testGetIssueType() {
		final BasicIssueType basicIssueType = client.getIssueClient().getIssue("TST-1", pm).getIssueType();
		final IssueType issueType = client.getMetadataClient().getIssueType(basicIssueType.getSelf(), pm);
		assertEquals("Bug", issueType.getName());
		assertEquals("A problem which impairs or prevents the functions of the product.", issueType.getDescription());
		Long expectedId = isJira5xOrNewer() ? 1L : null;
		assertEquals(expectedId, issueType.getId());
		assertTrue(issueType.getIconUri().toString().endsWith("bug.gif"));
	}

	@Test
	public void testGetIssueTypes() {
		if (!doesJiraSupportRestIssueLinking()) {
			return;
		}
		final Iterable<IssuelinksType> issueTypes = client.getMetadataClient().getIssueLinkTypes(pm);
		assertEquals(1, Iterables.size(issueTypes));
		final IssuelinksType issueType = Iterables.getOnlyElement(issueTypes);
		assertEquals("Duplicate", issueType.getName());
		assertEquals("is duplicated by", issueType.getInward());
		assertEquals("duplicates", issueType.getOutward());
	}

	@Test
	public void testGetStatus() {
		final BasicStatus basicStatus = client.getIssueClient().getIssue("TST-1", pm).getStatus();
		final Status status = client.getMetadataClient().getStatus(basicStatus.getSelf(), pm);
		assertEquals("The issue is open and ready for the assignee to start work on it.", status.getDescription());
		assertTrue(status.getIconUrl().toString().endsWith("status_open.gif"));
		assertEquals("Open", status.getName());
	}

	@Test
	public void testGetStatusNonExisting() throws Exception {
		final BasicStatus basicStatus = client.getIssueClient().getIssue("TST-1", pm).getStatus();
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND, "The status with id '" +
				TestUtil.getLastPathSegment(basicStatus.getSelf()) + "fake" +
				"' does not exist", new Runnable() {
			@Override
			public void run() {
				client.getMetadataClient().getStatus(TestUtil.toUri(basicStatus.getSelf() + "fake"), pm);
			}
		});
	}

	@Test
	public void testGetPriority() {
		final BasicPriority basicPriority = client.getIssueClient().getIssue("TST-2", pm).getPriority();
		final Priority priority = client.getMetadataClient().getPriority(basicPriority.getSelf(), pm);
		assertEquals(basicPriority.getSelf(), priority.getSelf());
		assertEquals("Major", priority.getName());
		assertEquals("#009900", priority.getStatusColor());
		assertEquals("Major loss of function.", priority.getDescription());
		final Long expectedId = isJira5xOrNewer() ? 3L : null;
		assertEquals(expectedId, priority.getId());
		assertTrue(priority.getIconUri().toString().startsWith(jiraUri.toString()));
		assertTrue(priority.getIconUri().toString().endsWith("/images/icons/priority_major.gif"));

	}

	@Test
	public void testGetResolution() {
		final Issue issue = client.getIssueClient().getIssue("TST-2", pm);
		assertNull(issue.getResolution());
		final Iterable<Transition> transitions = client.getIssueClient().getTransitions(issue, pm);
		final Transition resolveTransition = TestUtil.getTransitionByName(transitions, "Resolve Issue");

		client.getIssueClient().transition(issue, new TransitionInput(resolveTransition.getId()), pm);

		final Issue resolvedIssue = client.getIssueClient().getIssue("TST-2", pm);
		final BasicResolution basicResolution = resolvedIssue.getResolution();
		assertNotNull(basicResolution);

		final Resolution resolution = client.getMetadataClient().getResolution(basicResolution.getSelf(), pm);
		assertEquals(basicResolution.getName(), resolution.getName());
		assertEquals(basicResolution.getSelf(), resolution.getSelf());
		assertEquals("A fix for this issue is checked into the tree and tested.", resolution.getDescription());
	}
}
