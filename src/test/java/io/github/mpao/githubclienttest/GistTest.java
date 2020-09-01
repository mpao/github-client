package io.github.mpao.githubclienttest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import io.github.mpao.githubclient.Commit;
import io.github.mpao.githubclient.File;
import io.github.mpao.githubclient.Gist;
import org.junit.jupiter.api.Test;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;

public class GistTest {

    @Test
    public void createFile() {
        // *******************
        // 1. normal gist file
        // *******************
        File file = File.builder("yadayada.txt", null,false).build();
        assertNotNull(file);
        assertEquals(file.getFilename(), "yadayada.txt");
        // **************************************************************
        // 2. truncated gist file: get the whole content from the raw url
        // **************************************************************
        file = File.builder("google.txt", "https://www.google.com",true)
                .setSize(0L)
                .setLanguage("Java")
                .setType("application/text")
                .build();
        assertNotNull(file);
        assertTrue(file.getContent().contains("Google"));
    }

    @Test
    public void createCommit(){
        Commit commit = Commit.builder("abc").build();
        assertNotNull(commit);
        assertEquals(commit.getVersion(), "abc");
    }

    @Test
    public void createGist(){
        // **************
        // 1. normal gist
        // **************
        Gist gist = Gist.builder("abc", false).build();
        assertNotNull(gist);
        assertEquals(gist.getId(), "abc");
        assertFalse(gist.isTruncated());
        // ****************************************
        // 2. truncated gist: over 300 files inside
        // ****************************************
        gist = Gist.builder("123", true).build();
        assertEquals(gist.getId(), "123");
        assertTrue(gist.isTruncated());
        //todo test constructor logics
    }

    @Test
    public void singleGistRequest() throws URISyntaxException, JsonProcessingException {
        // *******************
        // 1. gist exists
        // *******************
        CompletableFuture<String> response = TestUtils.asyncHttpGetRequest("https://api.github.com/gists/4f966095f890bb5cbc7c");
        Gist gist = new ObjectMapper().readValue(response.join(), Gist.class);
        System.out.println(gist);
        assertNotNull(gist);
        // ********************************
        // 2. ... and has at least one file
        // ********************************
        assertTrue(gist.getFiles().size() > 0 );
        // *************************************
        // 3. ... and this file has some content
        // *************************************
        File file = gist.getFiles().entrySet().iterator().next().getValue();
        assertNotNull(file.getContent());
        // *********************
        // 4. gist doesn't exist
        // *********************
        assertThrows(ValueInstantiationException.class, () -> {
            CompletableFuture<String> r = TestUtils.asyncHttpGetRequest("https://api.github.com/gists/yadayada");
            new ObjectMapper().readValue(r.join(), Gist.class);
        });
    }

    @Test
    public void listUsersPublicGists() throws URISyntaxException, JsonProcessingException {
        // **********************************************************
        // 1. This API doesn't have all the contents, it's an excerpt
        // **********************************************************
        CompletableFuture<String> response = TestUtils.asyncHttpGetRequest("https://api.github.com/users/mpao/gists");
        List<Gist> gists = new ObjectMapper().readValue(response.join(), new TypeReference<>() {});
        assertNotNull(gists);
        // *******************************
        // 2. Check all the file's content
        // *******************************
        gists.stream()
                .flatMap(g -> g.getFiles().values().stream()) // take from Map<String, File> all the files from each gists
                .map(File::getUrl)                            // take each urls from File objects
                .forEach( url -> {                            // check if file's content exists
                    try {
                        String content = TestUtils.asyncHttpGetRequest(url).join();
                        assertTrue( content != null && !content.isBlank());
                    } catch (URISyntaxException e) {
                        assert false;
                    }
                });

    }

}