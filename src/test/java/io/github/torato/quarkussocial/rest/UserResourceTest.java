package io.github.torato.quarkussocial.rest;

import io.github.torato.quarkussocial.domain.model.User;
import io.github.torato.quarkussocial.domain.repository.UserRepository;
import io.github.torato.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@QuarkusTest
class UserResourceTest {

    @Inject
    UserResource userResource;

    @Inject
    UserRepository repository;

    @Inject
    UserResourceTest(UserRepository repository) {
        this.repository = repository;
    }


    @Test
    public void shouldHandleValidUserParameters() {

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setAge(24);
        userRequest.setName("Test User");

        Response response = userResource.createUser(userRequest);
        assertNotNull(response.getEntity());
        assertEquals(200, response.getStatus());

        User user = (User) response.getEntity();
        assertEquals("Test User", user.getName());
        assertEquals(24, user.getAge());
    }

    @Test
    public void shouldHandleNullUserParameters() {
        UserResource userResource = new UserResource(repository);

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setAge(null);
        userRequest.setName(null);

        Response response = userResource.createUser(userRequest);
        assertEquals(400, response.getStatus());
        assertEquals("Invalid input data", response.getEntity());
    }

    @Test
    public void shouldHandleEmptyUserName() {
        UserResource userResource = new UserResource(repository);

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setAge(24);
        userRequest.setName("");

        Response response = userResource.createUser(userRequest);
        assertEquals(400, response.getStatus());
        assertEquals("Invalid input data", response.getEntity());
    }

    @Test
    @Transactional
    public void shouldHandleExistingUsersList() {
        User user = new User();
        user.setAge(24);
        user.setName("Test User");


        Response response = userResource.listAllUsers();
        assertNotNull(response.getEntity());
        assertEquals(200, response.getStatus());

        List<User> userList = (List<User>) response.getEntity();
        assertFalse(userList.isEmpty());
        assertTrue(userList.stream().anyMatch(user1 -> user1.getName().equals("Test User") && user1.getAge() == 24));
    }

    @Test
    @Transactional
    public void shouldHandleExistingUserRetrieval() {
        User user = new User();
        user.setAge(24);
        user.setName("Test User");
        repository.persist(user);

        Response response = userResource.getUser("Test User");
        assertNotNull(response.getEntity());
        assertEquals(200, response.getStatus());

        User returnedUser = ((List<User>) response.getEntity()).get(0);
        assertEquals("Test User", returnedUser.getName());
        assertEquals(24, returnedUser.getAge());
    }

    @Test
    public void shouldHandleNonExistingUserRetrieval() {
        Response response = userResource.getUser("Random User");
        assertEquals(404, response.getStatus());
    }

    @Test
    public void shouldHandleUserWithNegativeAge() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setAge(null);
        userRequest.setName("Test User");

        Response response = userResource.createUser(userRequest);
        assertEquals(400, response.getStatus());
        assertEquals("Invalid input data", response.getEntity());
    }

    @Test
    public void shouldHandleUserNameWithSpecialCharacters() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setAge(24);
        userRequest.setName("Test$User!");

        Response response = userResource.createUser(userRequest);
        assertNotNull(response.getEntity());
        assertEquals(200, response.getStatus());

        User user = (User) response.getEntity();
        assertEquals("Test$User!", user.getName());
        assertEquals(24, user.getAge());
    }
}
