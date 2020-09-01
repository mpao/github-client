package io.github.mpao.githubclienttest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import io.github.mpao.githubclient.User;
import org.junit.jupiter.api.Test;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void createUser(){
        User user = User.builder("mpao").build();
        assertNotNull(user);
        assertEquals(user.getLogin(), "mpao");
    }

    @Test
    public void getUserInformation() throws URISyntaxException, JsonProcessingException {
        CompletableFuture<String> response = TestUtils.asyncHttpGetRequest("https://api.github.com/users/mpao");
        User user = new ObjectMapper().readValue(response.join(), User.class);
        System.out.println(user);
        assertNotNull(user);
        assertThrows(ValueInstantiationException.class, () -> {
            CompletableFuture<String> r = TestUtils.asyncHttpGetRequest("https://api.github.com/users/***mpao***");
            new ObjectMapper().readValue(r.join(), User.class);
        });
    }

}
