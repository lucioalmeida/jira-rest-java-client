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

package com.atlassian.jira.rest.client.domain;

import com.atlassian.jira.rest.client.AddressableEntity;
import com.atlassian.jira.rest.client.NamedEntity;
import com.google.common.base.Objects;

import java.net.URI;

/**
 * Basic information about a JIRA user
 *
 * @since v0.1
 */
public class BasicUser implements AddressableEntity, NamedEntity {
    private final String name;
    private final String displayName;
    private final URI self;

    public BasicUser(URI self, String name, String displayName) {
        this.self = self;
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }



    @Override
	public URI getSelf() {
        return self;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("name", name)
                .add("displayName", displayName).add("self", self).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BasicUser) {
            BasicUser that = (BasicUser) obj;
            return Objects.equal(this.self, that.self)
                    && Objects.equal(this.name, that.name)
                    && Objects.equal(this.displayName, that.displayName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(self, name, displayName);
    }

}
