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

import com.atlassian.jira.nimblefunctests.annotation.Restore;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.TestUtil;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.Session;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClient;
import com.atlassian.jira.rest.client.internal.json.TestConstants;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.atlassian.jira.rest.client.internal.json.TestConstants.*;

@Restore(TestConstants.DEFAULT_JIRA_DUMP_FILE)
public class JerseySessionRestClientTest extends AbstractJerseyRestClientTest {

	@Test
	public void testValidSession() {
		final Session session = client.getSessionClient().getCurrentSession(new NullProgressMonitor());
		assertEquals(ADMIN_USERNAME, session.getUsername());

	}

	@Test
	public void testInvalidCredentials() {
		client = new JerseyJiraRestClient(jiraUri, new BasicHttpAuthenticationHandler(ADMIN_USERNAME, ADMIN_PASSWORD + "invalid"));
		TestUtil.assertErrorCode(401, new Runnable() {
			@Override
			public void run() {
				client.getSessionClient().getCurrentSession(new NullProgressMonitor());
			}
		});
	}

	@Test
	public void testGetCurrentSession() throws Exception {
		final Session session = client.getSessionClient().getCurrentSession(new NullProgressMonitor());
		assertEquals(ADMIN_USERNAME, session.getUsername());

		// that is not a mistake - username and the password for this user is the same
		client = new JerseyJiraRestClient(jiraUri, new BasicHttpAuthenticationHandler(TestConstants.USER1.getName(),
				TestConstants.USER1.getName()));
		final Session session2 = client.getSessionClient().getCurrentSession(new NullProgressMonitor());
		assertEquals(TestConstants.USER1.getName(), session2.getUsername());
		final DateTime lastFailedLoginDate = session2.getLoginInfo().getLastFailedLoginDate();

		final JerseyJiraRestClient client2 = new JerseyJiraRestClient(jiraUri, new BasicHttpAuthenticationHandler(TestConstants.USER1.getName(),
				"bad-password"));
		final DateTime now = new DateTime();
		TestUtil.assertErrorCode(401, new Runnable() {
			@Override
			public void run() {
				client2.getSessionClient().getCurrentSession(new NullProgressMonitor());
			}
		});
		while (!new DateTime().isAfter(lastFailedLoginDate)) {
			Thread.sleep(20);
		}

		final Session sessionAfterFailedLogin = client.getSessionClient().getCurrentSession(new NullProgressMonitor());
		assertTrue(sessionAfterFailedLogin.getLoginInfo().getLastFailedLoginDate().isAfter(lastFailedLoginDate));
		assertTrue(sessionAfterFailedLogin.getLoginInfo().getLastFailedLoginDate().isAfter(now));
	}

}
