package io.github.mpao.githubclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Commit {

    private User user;
    private String version;
    private String url;
    @JsonProperty("committed_at") private Timestamp createAt;

    @JsonCreator
    private Commit(@NonNull @JsonProperty("version") String version) throws NullPointerException{
        this.version = version;
    }

    public static CommitBuilder builder(final String version) {
        return new CommitBuilder().version(version);
    }

}
