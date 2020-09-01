package io.github.mpao.githubclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import static java.time.temporal.ChronoUnit.SECONDS;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class File {

    private String filename;
    private String type;
    private String language;
    private long size;
    private boolean truncated;
    private String content;
    @JsonProperty("raw_url") private String url;

    @JsonCreator
    private File(
            @NonNull @JsonProperty("filename") String filename,
            @JsonProperty("raw_url") String url,
            @JsonProperty("truncated") boolean truncated
    ) throws NullPointerException {
        this.filename = filename;
        this.url = url;
        this.truncated = truncated;
    }

    public void setContent(String content){
        // todo: Be aware that for files larger than ten megabytes, you'll need to clone the gist via the URL provided by git_pull_url.
        if(truncated | content == null){
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .timeout(Duration.of(60, SECONDS))
                        .GET()
                        .build();
                CompletableFuture<String> response = HttpClient.newHttpClient()
                        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body);
                this.content = response.join();
            } catch (URISyntaxException e) {
                this.content = content;
            }
        }else {
            this.content = content;
        }
    }

    public static Builder builder(final String filename, final String url, final boolean truncated) {
        return new Builder()
                .setFilename(filename)
                .setUrl(url)
                .setTruncated(truncated);
    }

    //region Builder
    public static final class Builder {
        private String filename;
        private String type;
        private String language;
        private long size;
        private boolean truncated;
        private String content;
        private String url;

        private Builder() { }

        public Builder setFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setLanguage(String language) {
            this.language = language;
            return this;
        }

        public Builder setSize(long size) {
            this.size = size;
            return this;
        }

        public Builder setTruncated(boolean truncated) {
            this.truncated = truncated;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public File build() {
            File file = new File(filename, url, truncated);
            file.setContent(content);
            file.setType(type);
            file.setLanguage(language);
            file.setSize(size);
            file.setUrl(url);
            return file;
        }

    }
    //endregion

}
