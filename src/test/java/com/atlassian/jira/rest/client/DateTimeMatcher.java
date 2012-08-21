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

import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.internal.matchers.TypeSafeMatcher;

public class DateTimeMatcher extends TypeSafeMatcher<DateTime> {
    private final DateTime expected;

    public DateTimeMatcher(DateTime expected) {
        this.expected = expected;
    }


    public static DateTimeMatcher isEqual(DateTime dateTime) {
        return new DateTimeMatcher(dateTime);
    }

    @Override
	public boolean matchesSafely(DateTime given) {
		return expected == null ? given == null : expected.isEqual(given);
	}

    @Override
	public void describeTo(Description description) {
        description.appendText("datetime equal to  " + expected);
    }
}
