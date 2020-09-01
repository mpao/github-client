package io.github.mpao.githubclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gist {

    private String id;
    private String description;
    private int comments;
    private User owner;
    private boolean truncated;
    private List<Commit> history;
    private Map<String, File> files;
    @JsonProperty("html_url") private String url;
    @JsonProperty("created_at") private Timestamp createAt;
    @JsonProperty("updated_at") private Timestamp lastModified;
    @JsonProperty("git_pull_url") private String gitUrl;
    @JsonProperty("public") private boolean visible;
    @JsonProperty("comments_url") private String commentsUrl;

    @JsonCreator
    private Gist(@NonNull @JsonProperty("id") String id, @JsonProperty("truncated") boolean truncated) throws NullPointerException{
        // The Gist API provides up to one megabyte of content for each file in the gist.
        // Each file returned for a gist through the API has a key called truncated.
        // If truncated is true, the file is too large and only a portion of the contents were returned in content.
        // If you need the full contents of the file, you can make a GET request to the URL specified by raw_url.
        // Be aware that for files larger than ten megabytes, you'll need to clone the gist via the URL provided by git_pull_url.
        // In addition to a specific file's contents being truncated, the entire files list may be truncated if the total number exceeds 300 files.
        // If the top level truncated key is true, only the first 300 files have been returned in the files list.
        // If you need to fetch all of the gist's files, you'll need to clone the gist via the URL provided by git_pull_url.
        this.id = id;
        this.truncated = truncated;
        if(truncated){
            //todo truncated cases: more than 300 files
            System.out.println("Truncated gist");
        }
    }

    public static GistBuilder builder(final String id, final boolean truncated) {
        return new GistBuilder()
                .id(id)
                .truncated(truncated);
    }

}