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

import com.atlassian.jira.rest.client.domain.BasicIssueType;
import org.codehaus.jettison.json.JSONException;
import org.junit.Test;

import static com.atlassian.jira.rest.client.TestUtil.toUri;
import static org.junit.Assert.assertEquals;

public class BasicIssueTypeJsonParserTest {
    @Test
    public void testParse() throws JSONException {
        BasicIssueTypeJsonParser parser = new BasicIssueTypeJsonParser();
        final BasicIssueType issueType = parser.parse(ResourceUtil.getJsonObjectFromResource("/json/issueType/valid.json"));
        assertEquals(new BasicIssueType(toUri("http://localhost:8090/jira/rest/api/latest/issueType/1"), 1L, "Bug", true), issueType);
    }

	@Test
	public void testParseWithoutId() throws JSONException {
		BasicIssueTypeJsonParser parser = new BasicIssueTypeJsonParser();
		final BasicIssueType issueType = parser.parse(ResourceUtil.getJsonObjectFromResource("/json/issueType/valid-without-id.json"));
		assertEquals(new BasicIssueType(toUri("http://localhost:8090/jira/rest/api/latest/issueType/1"), null, "Bug", true), issueType);
	}

}
