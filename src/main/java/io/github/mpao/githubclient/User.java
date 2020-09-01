package io.github.mpao.githubclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String login;
    private long id;
    private String name;
    private String blog;
    @JsonProperty("avatar_url") private String avatar;
    @JsonProperty("gravatar_id") private String gravatar;
    @JsonProperty("html_url") private String homepage;
    @JsonProperty("repos_url") private String repos;

    @JsonCreator
    private User(@NonNull @JsonProperty("login") String login) throws NullPointerException{
        this.login = login;
    }

    public static UserBuilder builder(final String login) {
        return new UserBuilder().login(login);
    }

}