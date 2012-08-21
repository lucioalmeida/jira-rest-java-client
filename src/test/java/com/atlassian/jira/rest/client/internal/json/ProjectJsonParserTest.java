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

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.TestUtil;
import com.atlassian.jira.rest.client.domain.IssueType;
import com.atlassian.jira.rest.client.domain.Project;
import com.google.common.collect.Iterables;
import org.codehaus.jettison.json.JSONException;
import org.hamcrest.collection.IsEmptyIterable;
import org.joda.time.DateMidnight;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;

// Ignore "May produce NPE" warnings, as we know what we are doing in tests
@SuppressWarnings("ConstantConditions")
public class ProjectJsonParserTest {
	private final ProjectJsonParser parser = new ProjectJsonParser();

	@Test
	public void testParse() throws Exception {
		final Project project = parser.parse(ResourceUtil.getJsonObjectFromResource("/json/project/valid.json"));
		assertEquals(TestUtil.toUri("http://localhost:8090/jira/rest/api/latest/project/TST"), project.getSelf());
		assertEquals("This is my description here.\r\nAnother line.", project.getDescription());
		assertEquals(TestConstants.USER_ADMIN, project.getLead());
		assertEquals("http://example.com", project.getUri().toString());
		assertEquals("TST", project.getKey());
		assertThat(project.getVersions(), containsInAnyOrder(TestConstants.VERSION_1, TestConstants.VERSION_1_1));
		assertThat(project.getComponents(), containsInAnyOrder(TestConstants.BCOMPONENT_A, TestConstants.BCOMPONENT_B));
        assertNull(project.getName());
		assertThat(project.getIssueTypes(), IsEmptyIterable.<IssueType>emptyIterable());
	}

	@Test
	public void testParseProjectWithNoUrl() throws JSONException {
		final Project project = parser.parse(ResourceUtil.getJsonObjectFromResource("/json/project/project-no-url.json"));
		assertEquals("MYT", project.getKey());
		assertNull(project.getUri());
		assertNull(project.getDescription());
	}

	@Test
	public void testParseProjectInJira4x4() throws JSONException, URISyntaxException {
		final Project project = parser.parse(ResourceUtil.getJsonObjectFromResource("/json/project/project-jira-4-4.json"));
		assertEquals("TST", project.getKey()); //2010-08-25
		assertEquals(new DateMidnight(2010, 8, 25).toInstant(), Iterables.getLast(project.getVersions()).getReleaseDate().toInstant());
        assertEquals("Test Project", project.getName());
		assertThat(project.getIssueTypes(), containsInAnyOrder(
				new IssueType(new URI("http://localhost:2990/jira/rest/api/latest/issueType/1"), null, "Bug", false, null, null),
				new IssueType(new URI("http://localhost:2990/jira/rest/api/latest/issueType/2"), null, "New Feature", false, null, null),
				new IssueType(new URI("http://localhost:2990/jira/rest/api/latest/issueType/3"), null, "Task", false, null, null),
				new IssueType(new URI("http://localhost:2990/jira/rest/api/latest/issueType/4"), null, "Improvement", false, null, null),
				new IssueType(new URI("http://localhost:2990/jira/rest/api/latest/issueType/5"), null, "Sub-task", true, null, null)
		));
	}

	@Test
	public void testParseProjectInJira5x0() throws JSONException, URISyntaxException {
		final Project project = parser.parse(ResourceUtil.getJsonObjectFromResource("/json/project/project-jira-5-0.json"));
		assertEquals("TST", project.getKey());
		assertEquals(new DateMidnight(2010, 8, 25).toInstant(), Iterables.getLast(project.getVersions()).getReleaseDate().toInstant());
		assertEquals("Test Project", project.getName());
		assertThat(project.getIssueTypes(), containsInAnyOrder(
				new IssueType(new URI("http://localhost:2990/jira/rest/api/latest/issuetype/1"), 1L, "Bug", false, "A problem which impairs or prevents the functions of the product.", new URI("http://localhost:2990/jira/images/icons/bug.gif")),
				new IssueType(new URI("http://localhost:2990/jira/rest/api/latest/issuetype/2"), 2L, "New Feature", false, "A new feature of the product, which has yet to be developed.", new URI("http://localhost:2990/jira/images/icons/newfeature.gif")),
				new IssueType(new URI("http://localhost:2990/jira/rest/api/latest/issuetype/3"), 3L, "Task", false, "A task that needs to be done.", new URI("http://localhost:2990/jira/images/icons/task.gif")),
				new IssueType(new URI("http://localhost:2990/jira/rest/api/latest/issuetype/4"), 4L, "Improvement", false, "An improvement or enhancement to an existing feature or task.", new URI("http://localhost:2990/jira/images/icons/improvement.gif")),
				new IssueType(new URI("http://localhost:2990/jira/rest/api/latest/issuetype/5"), 5L, "Sub-task", true, "The sub-task of the issue", new URI("http://localhost:2990/jira/images/icons/issue_subtask.gif"))
		));
	}
}
