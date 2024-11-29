package io.github.torato.quarkussocial.rest;

import io.github.torato.quarkussocial.domain.model.User;
import io.github.torato.quarkussocial.domain.repository.UserRepository;
import io.github.torato.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserRepository repository;

    @Inject
    public UserResource(UserRepository repository) {
        this.repository = repository;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest) {

        User user = new User();

        if (userRequest.getAge()

                == null || userRequest.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input data").build();
        }

        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());
        repository.persist(user);

        return Response.ok(user).build();

    }

    @GET
    public Response listAllUsers() {
        List<User> userList = new ArrayList<>(repository.listAll());
        if(userList.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).entity("Not found Users").build();
        }
        return Response.ok(userList).build();
    }

    @GET
    @Path("{name}")
    public Response getUser(@PathParam("name") String name) {
        PanacheQuery<User> query = repository.find("name", name);
        if (query.list().isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(query.list()).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id , User user){
        User userFound = repository.findById(id);

        if(userFound == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        userFound.setName(user.getName());
        userFound.setAge(user.getAge());

        return Response.ok().build();
    }
}
