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

import com.atlassian.jira.nimblefunctests.annotation.JiraBuildNumberDependent;
import com.atlassian.jira.nimblefunctests.annotation.Restore;
import com.atlassian.jira.rest.client.BasicComponentNameExtractionFunction;
import com.atlassian.jira.rest.client.IntegrationTestUtil;
import com.atlassian.jira.rest.client.TestUtil;
import com.atlassian.jira.rest.client.domain.AssigneeType;
import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.Component;
import com.atlassian.jira.rest.client.domain.input.ComponentInput;
import com.atlassian.jira.rest.client.internal.ServerVersionConstants;
import com.atlassian.jira.rest.client.internal.json.TestConstants;
import com.google.common.collect.Iterables;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static com.atlassian.jira.rest.client.domain.EntityHelper.findEntityByName;
import static com.atlassian.jira.rest.client.internal.ServerVersionConstants.BN_JIRA_4_4;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;

@Restore(TestConstants.DEFAULT_JIRA_DUMP_FILE)
public class JerseyComponentRestClientTest extends AbstractJerseyRestClientTest {

	@Test
	public void testGetComponent() throws Exception {
		final BasicComponent basicComponent = findEntityByName(client.getProjectClient().getProject("TST", pm).getComponents(), "Component A");
		final Component component = client.getComponentClient().getComponent(basicComponent.getSelf(), pm);
		assertEquals("Component A", component.getName());
		assertEquals("this is some description of component A", component.getDescription());
		assertEquals(IntegrationTestUtil.USER_ADMIN_LATEST, component.getLead());
	}

	@Test
	@JiraBuildNumberDependent(BN_JIRA_4_4)
	public void testGetComponentOnJira4xOrNewerShouldContainNotNullId() throws Exception {
		final BasicComponent basicComponent = findEntityByName(client.getProjectClient().getProject("TST", pm).getComponents(), "Component A");
		final Component component = client.getComponentClient().getComponent(basicComponent.getSelf(), pm);
		assertEquals("Component A", component.getName());
		assertEquals("this is some description of component A", component.getDescription());
		assertEquals(Long.valueOf(10000), component.getId());
		assertEquals(IntegrationTestUtil.USER_ADMIN_LATEST, component.getLead());
	}

	@Test
	public void testGetInvalidComponent() throws Exception {
		final BasicComponent basicComponent = Iterables.get(client.getProjectClient().getProject("TST", pm).getComponents(), 0);
		final String uriForUnexistingComponent = basicComponent.getSelf().toString() + "1234";
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND,  "The component with id "
				+ TestUtil.getLastPathSegment(basicComponent.getSelf()) + "1234 does not exist.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().getComponent(TestUtil.toUri(uriForUnexistingComponent), pm);
			}
		});
	}

	@Test
	public void testGetComponentFromRestrictedProject() throws Exception {
		final BasicComponent basicComponent = Iterables.getOnlyElement(client.getProjectClient().getProject("RST", pm).getComponents());
		assertEquals("One Great Component", client.getComponentClient().getComponent(basicComponent.getSelf(), pm).getName());

		// now as unauthorized user
		setClient(TestConstants.USER2_USERNAME, TestConstants.USER2_PASSWORD);
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND, IntegrationTestUtil.TESTING_JIRA_5_OR_NEWER ?
				      	"The component with id 10010 does not exist." : "The user user does not have permission to complete this operation.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().getComponent(basicComponent.getSelf(), pm).getName();
			}
		});

		setAnonymousMode();
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND, IntegrationTestUtil.TESTING_JIRA_5_OR_NEWER ?
				      	"The component with id 10010 does not exist." : "This user does not have permission to complete this operation.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().getComponent(basicComponent.getSelf(), pm).getName();
			}
		});
	}

	@Test
	@JiraBuildNumberDependent(BN_JIRA_4_4)
	public void testCreateAndRemoveComponent() {
		final Iterable<BasicComponent> components = client.getProjectClient().getProject("TST", pm).getComponents();
		assertEquals(2, Iterables.size(components));
		final BasicComponent basicComponent = Iterables.get(components, 0);
		final BasicComponent basicComponent2 = Iterables.get(components, 1);
		final String componentName = "my component";
		final ComponentInput componentInput = new ComponentInput(componentName, "a description", null, null);
		final Component component = client.getComponentClient().createComponent("TST", componentInput, pm);
		assertEquals(componentInput.getName(), component.getName());
		assertEquals(componentInput.getDescription(), component.getDescription());
		assertNull(component.getLead());
		assertProjectHasComponents(basicComponent.getName(), basicComponent2.getName(), componentName);

		client.getComponentClient().removeComponent(basicComponent.getSelf(), null, pm);
		assertProjectHasComponents(basicComponent2.getName(), componentName);
		client.getComponentClient().removeComponent(basicComponent2.getSelf(), null, pm);
		assertProjectHasComponents(componentName);
		client.getComponentClient().removeComponent(component.getSelf(), null, pm);
		assertProjectHasComponents();

	}

	@Test
	@JiraBuildNumberDependent(BN_JIRA_4_4)
	public void testCreateAndRemoveComponentAsUnauthorizedUsers() {
		final Iterable<BasicComponent> components = client.getProjectClient().getProject("TST", pm).getComponents();
		assertEquals(2, Iterables.size(components));
		final BasicComponent basicComponent = Iterables.get(components, 0);

		final ComponentInput componentInput = new ComponentInput("my component", "a description", null, null);
		setUser1();

		final Response.Status expectedForbiddenErrorCode = (doesJiraReturnCorrectErrorCodeForForbiddenOperation()) ? Response.Status.FORBIDDEN : Response.Status.UNAUTHORIZED;
		TestUtil.assertErrorCode(expectedForbiddenErrorCode, "The user wseliga does not have permission to complete this operation.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().removeComponent(basicComponent.getSelf(), null, pm);
			}
		});
		TestUtil.assertErrorCode(expectedForbiddenErrorCode, "You cannot edit the configuration of this project.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().createComponent("TST", componentInput, pm);
			}
		});

		setAnonymousMode();
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND, IntegrationTestUtil.TESTING_JIRA_5_OR_NEWER ?
				      	"The component with id 10000 does not exist." : "This user does not have permission to complete this operation.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().removeComponent(basicComponent.getSelf(), null, pm);
			}
		});

		if (IntegrationTestUtil.TESTING_JIRA_5_OR_NEWER) {
			TestUtil.assertErrorCode(Response.Status.NOT_FOUND, "No project could be found with key 'TST'.", new Runnable() {
				@Override
				public void run() {
					client.getComponentClient().createComponent("TST", componentInput, pm);
				}
			});
		} else {
			// IMO for anonymous access still Response.Status.UNAUTHORIZED should be returned - JRADEV-7671
			TestUtil.assertErrorCode(expectedForbiddenErrorCode, "You cannot edit the configuration of this project.", new Runnable() {
				@Override
				public void run() {
					client.getComponentClient().createComponent("TST", componentInput, pm);
				}
			});
		}


		setAdmin();
		// now let's try to add a component with colliding name
		final ComponentInput dupeComponentInput = new ComponentInput(basicComponent.getName(), "a description", null, null);
		TestUtil.assertErrorCode(Response.Status.BAD_REQUEST, "A component with the name Component A already exists in this project.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().createComponent("TST", dupeComponentInput, pm);
			}
		});

		// now let's try to add a component for a non existing project
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND, "No project could be found with key 'FAKE'.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().createComponent("FAKE", componentInput, pm);
			}
		});

	}


	@SuppressWarnings({"ConstantConditions"})
	@Test
	@JiraBuildNumberDependent(BN_JIRA_4_4)
	public void testCreateComponentWithLead() {
		final ComponentInput componentInput = new ComponentInput("my component name", "a description", "admin", AssigneeType.COMPONENT_LEAD);
		final Component component = client.getComponentClient().createComponent("TST", componentInput, pm);
		assertNotNull(component.getAssigneeInfo());
		assertEquals(IntegrationTestUtil.USER_ADMIN_LATEST, component.getAssigneeInfo().getAssignee());
		assertEquals(AssigneeType.COMPONENT_LEAD, component.getAssigneeInfo().getAssigneeType());
		assertTrue(component.getAssigneeInfo().isAssigneeTypeValid());
		assertEquals(IntegrationTestUtil.USER_ADMIN_LATEST, component.getAssigneeInfo().getRealAssignee());
		assertEquals(AssigneeType.COMPONENT_LEAD, component.getAssigneeInfo().getRealAssigneeType());

		final ComponentInput componentInput2 = new ComponentInput("my component name2", "a description", IntegrationTestUtil.USER1.getName(), AssigneeType.UNASSIGNED);
		final Component component2 = client.getComponentClient().createComponent("TST", componentInput2, pm);
		assertNotNull(component2.getAssigneeInfo());
		assertNull(component2.getAssigneeInfo().getAssignee());
		assertEquals(AssigneeType.UNASSIGNED, component2.getAssigneeInfo().getAssigneeType());
		assertFalse(component2.getAssigneeInfo().isAssigneeTypeValid());
		assertEquals(IntegrationTestUtil.USER_ADMIN_LATEST, component2.getAssigneeInfo().getRealAssignee());
		assertEquals(AssigneeType.PROJECT_DEFAULT, component2.getAssigneeInfo().getRealAssigneeType());
	}


	@Test
	@JiraBuildNumberDependent(BN_JIRA_4_4)
	public void testUpdateComponent() {
		final BasicComponent basicComponent = Iterables.get(client.getProjectClient().getProject("TST", pm).getComponents(), 0);
		final Component component = client.getComponentClient().getComponent(basicComponent.getSelf(), pm);
		final String newName = basicComponent.getName() + "updated";
		Component adjustedComponent = new Component(component.getSelf(), component.getId(), newName, component.getDescription(), component.getLead(), component.getAssigneeInfo());

		Component updatedComponent = client.getComponentClient().updateComponent(basicComponent.getSelf(), new ComponentInput(newName, null, null, null), pm);
		assertEquals(adjustedComponent, updatedComponent);
		assertEquals(adjustedComponent, client.getComponentClient().getComponent(basicComponent.getSelf(), pm));

		final String newDescription = "updated description";
		adjustedComponent = new Component(component.getSelf(), component.getId(), newName, newDescription, IntegrationTestUtil.USER1_LATEST, component.getAssigneeInfo());
		updatedComponent = client.getComponentClient().updateComponent(basicComponent.getSelf(), new ComponentInput(null, newDescription, IntegrationTestUtil.USER1.getName(), null), pm);
		assertEquals(adjustedComponent, updatedComponent);

		adjustedComponent = new Component(component.getSelf(), component.getId(), newName, newDescription, IntegrationTestUtil.USER1_LATEST,
				new Component.AssigneeInfo(IntegrationTestUtil.USER1_LATEST, AssigneeType.COMPONENT_LEAD, IntegrationTestUtil.USER1_LATEST, AssigneeType.COMPONENT_LEAD, true));

		updatedComponent = client.getComponentClient().updateComponent(basicComponent.getSelf(), new ComponentInput(null, newDescription, IntegrationTestUtil.USER1.getName(), AssigneeType.COMPONENT_LEAD), pm);
		assertEquals(adjustedComponent, updatedComponent);


		// now with non-assignable assignee (thus we are inheriting assignee from project settings and component-level settings are ignored)
		adjustedComponent = new Component(component.getSelf(), component.getId(), newName, newDescription, IntegrationTestUtil.USER2_LATEST,
				new Component.AssigneeInfo(IntegrationTestUtil.USER2_LATEST, AssigneeType.COMPONENT_LEAD, IntegrationTestUtil.USER_ADMIN_LATEST, AssigneeType.PROJECT_DEFAULT, false));

		updatedComponent = client.getComponentClient().updateComponent(basicComponent.getSelf(), new ComponentInput(null, newDescription, IntegrationTestUtil.USER2.getName(), AssigneeType.COMPONENT_LEAD), pm);
		assertEquals(adjustedComponent, updatedComponent);

	}


	@Test
	@JiraBuildNumberDependent(BN_JIRA_4_4)
	public void testGetComponentRelatedIssuesCount() {
		final BasicComponent bc = findEntityByName(client.getProjectClient().getProject("TST", pm).getComponents(), "Component A");
		assertEquals(1, client.getComponentClient().getComponentRelatedIssuesCount(bc.getSelf(), pm));
		final ComponentInput componentInput = new ComponentInput("my component name", "a description", "admin", AssigneeType.COMPONENT_LEAD);
		final Component component = client.getComponentClient().createComponent("TST", componentInput, pm);
		assertEquals(0, client.getComponentClient().getComponentRelatedIssuesCount(component.getSelf(), pm));

		client.getComponentClient().removeComponent(bc.getSelf(), component.getSelf(), pm);
		assertEquals(1, client.getComponentClient().getComponentRelatedIssuesCount(component.getSelf(), pm));

		// smelly error code/message returned here - JRA-25062
		setAnonymousMode();
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND, IntegrationTestUtil.TESTING_JIRA_5_OR_NEWER ?
				      	"The component with id 10000 does not exist." : "This user does not have permission to complete this operation.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().getComponentRelatedIssuesCount(component.getSelf(), pm);
			}
		});

		setAdmin();
		final BasicComponent restrictedComponent = Iterables.getOnlyElement(client.getProjectClient().getProject("RST", pm).getComponents());
		setUser1();
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND, IntegrationTestUtil.TESTING_JIRA_5_OR_NEWER ?
				      	"The component with id 10010 does not exist." : "The user wseliga does not have permission to complete this operation.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().getComponentRelatedIssuesCount(restrictedComponent.getSelf(), pm);
			}
		});

		setAdmin();
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND, "The component with id " + TestUtil.getLastPathSegment(restrictedComponent.getSelf())
				+ "999 does not exist.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().getComponentRelatedIssuesCount(TestUtil.toUri(restrictedComponent.getSelf() + "999"), pm);
			}
		});

	}


	private boolean doesJiraReturnCorrectErrorCodeForForbiddenOperation() {
		return client.getMetadataClient().getServerInfo(pm).getBuildNumber() >= ServerVersionConstants.BN_JIRA_5;
	}


	private void assertProjectHasComponents(String ...names) {
		assertThat(Iterables.transform(client.getProjectClient().getProject("TST", pm).getComponents(),
				new BasicComponentNameExtractionFunction()), containsInAnyOrder(names));
	}

}
