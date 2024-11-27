package io.github.torato.quarkussocial.rest;

import io.github.torato.quarkussocial.domain.model.User;
import io.github.torato.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
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

    @POST
    @Transactional
    public Response createUser( CreateUserRequest userRequest){

        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());
        user.persist();

        return Response.ok(user).build();

    }

    @GET
    public Response listAllUsers(){
        List<User> userList = new ArrayList<>(User.listAll());
        return Response.ok(userList).build();
    }

    @GET
    @Path("{name}")
    public Response getUser(@PathParam("name") String name){
        PanacheQuery<User> query = User.find("name", name);
        if(query.list().isEmpty()){return Response.status(Response.Status.NOT_FOUND).build();}

        return Response.ok(query.list()).build();
    }
}
