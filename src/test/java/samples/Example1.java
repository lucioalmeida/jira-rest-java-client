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

package samples;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.json.CommonIssueJsonParser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

/**
 * A sample code how to use JRJC library 
 *
 * @since v0.1
 */
public class Example1 {
	public static void main(String[] args) throws URISyntaxException {
		final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
		final URI jiraServerUri = new URI("http://ta-dev.dyndns.biz:9980");
		final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, "admin", "zzz666");
		final NullProgressMonitor pm = new NullProgressMonitor();

        final Issue issue = restClient.getIssueClient().getIssue("TEST-208", pm);

        IssueInputBuilder issueInputBuilder = new IssueInputBuilder(issue.getProject().getKey(), 1L);
        issueInputBuilder.setSummary("short summary");

        try {
            restClient.getIssueClient().updateIssue(issue.getKey(), issueInputBuilder.build(), null);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // first let's get and print all visible projects
		final Iterable<BasicProject> allProjects = restClient.getProjectClient().getAllProjects(pm);
		for (BasicProject project : allProjects) {
			System.out.println(project);
		}

		// let's now print all issues matching a JQL string (here: all assigned issues)
/*		final SearchResult<Issue> searchResult = restClient.getSearchClient().searchJql("assignee is not EMPTY","\u002A"+"all", pm, new CommonIssueJsonParser());
		for (Issue issue : searchResult.getIssues()) {
			System.out.println(issue);
		}

		final Issue issue = restClient.getIssueClient().getIssue("TST-1", pm);

		System.out.println(issue);

		// now let's vote for it
		restClient.getIssueClient().vote(issue.getVotesUri(), pm);

		// now let's watch it
		restClient.getIssueClient().watch(issue.getWatchers().getSelf(), pm);

		// now let's start progress on this issue
		final Iterable<Transition> transitions = restClient.getIssueClient().getTransitions(issue.getTransitionsUri(), pm);
		final Transition startProgressTransition = getTransitionByName(transitions, "Start Progress");
		restClient.getIssueClient().transition(issue.getTransitionsUri(), new TransitionInput(startProgressTransition.getId()), pm);

		// and now let's resolve it as Incomplete
		final Transition resolveIssueTransition = getTransitionByName(transitions, "Resolve Issue");
		Collection<FieldInput> fieldInputs = Arrays.asList(new FieldInput("resolution", "Incomplete"));
		final TransitionInput transitionInput = new TransitionInput(resolveIssueTransition.getId(), fieldInputs, Comment.valueOf("My comment"));
		restClient.getIssueClient().transition(issue.getTransitionsUri(), transitionInput, pm);*/

	}

	private static Transition getTransitionByName(Iterable<Transition> transitions, String transitionName) {
		for (Transition transition : transitions) {
			if (transition.getName().equals(transitionName)) {
				return transition;
			}
		}
		return null;
	}

}
