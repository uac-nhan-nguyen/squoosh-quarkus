package org.acme

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

data class Hello(val message: String);

@Path("/hello")
class GreetingResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun hello() = Hello("Hello from RESTEasy Reactive")
}