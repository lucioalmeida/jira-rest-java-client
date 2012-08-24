package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.domain.Issue;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Collection;

public class CommonIssueJsonParser implements JsonParser<Collection<Issue>> {

    @Override
    public Collection<Issue> parse(JSONObject json) throws JSONException {

        JsonParser<Issue> issueParser = new IssueJsonParser(json.optJSONObject("names"), json.optJSONObject("schema"));

        return JsonParseUtil.parseJsonArray(json.getJSONArray("issues"), issueParser);
    }
}
