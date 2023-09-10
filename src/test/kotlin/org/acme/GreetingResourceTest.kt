package org.acme

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.lessThan
import org.junit.jupiter.api.Test

@QuarkusTest
class GreetingResourceTest {

    @Test
    fun testHelloEndpoint() {
        given()
            .`when`().get("/greeting/hello")
            .then()
            .statusCode(200)
            .body("message", equalTo("Hello from RESTEasy Reactive"))
    }

    @Test
    fun testSystemTime() {
        val item = given()
            .`when`().get("/greeting/time")
            .then()
            .statusCode(200)
            .body("time", lessThan(System.currentTimeMillis()))
            .body("rand", lessThan(100))
    }
}