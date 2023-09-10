package org.acme

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

data class Hello(val message: String);
data class GreetingTime(val time: Long, val rand: Int);

@Path("/greeting")
class GreetingResource {

    @GET
    @Path("/hello")
    @Produces(MediaType.APPLICATION_JSON)
    fun hello() = Hello("Hello from RESTEasy Reactive")

    @GET
    @Path("/time")
    @Produces(MediaType.APPLICATION_JSON)
    fun time() = GreetingTime(System.currentTimeMillis(), (Math.random() * 100).toInt())
}