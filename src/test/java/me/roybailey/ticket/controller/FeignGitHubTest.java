package me.roybailey.ticket.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import lombok.Data;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Ignore
public class FeignGitHubTest {

    private static final Logger LOG = LoggerFactory.getLogger(FeignGitHubTest.class);

    interface GitHub {

        @Data
        class Repository {
            String name;
        }

        @Data
        class Contributor {
            String login;
        }

        @RequestLine("GET /users/{username}/repos?sort=full_name")
        List<Repository> repos(@Param("username") String owner);

        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);

        /**
         * Lists all contributors for all repos owned by a user.
         */
        default List<String> contributors(String owner) {
            return repos(owner).stream()
                    .flatMap(repo -> contributors(owner, repo.name).stream())
                    .map(c -> c.login)
                    .distinct()
                    .collect(Collectors.toList());
        }

        static GitHub connect() {
            ObjectMapper mapper = new ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .configure(SerializationFeature.INDENT_OUTPUT, true)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Decoder decoder = new JacksonDecoder(mapper);
            return Feign.builder()
                    .decoder(decoder)
                    .errorDecoder(new GitHubErrorDecoder(decoder))
                    .logger(new feign.Logger.ErrorLogger())
                    .logLevel(feign.Logger.Level.BASIC)
                    .target(GitHub.class, "https://api.github.com");
        }
    }


    static class GitHubClientError extends RuntimeException {
        private String message; // parsed from json

        @Override
        public String getMessage() {
            return message;
        }
    }

    static class GitHubErrorDecoder implements ErrorDecoder {

        final Decoder decoder;
        final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

        GitHubErrorDecoder(Decoder decoder) {
            this.decoder = decoder;
        }

        @Override
        public Exception decode(String methodKey, Response response) {
            try {
                Object errorObject = decoder.decode(response, GitHubClientError.class);
                LOG.warn("GitHubErrorDecoder {}",errorObject);
                return (Exception) errorObject;
            } catch (IOException fallbackToDefault) {
                return defaultDecoder.decode(methodKey, response);
            }
        }
    }


    @Test
    public void testFeignGitHub() {
        GitHub github = GitHub.connect();
        String owner = "roybailey";

        LOG.info("Fetching list of the repositories for {}", owner);
        List<GitHub.Repository> repositories = github.repos(owner);
        for (GitHub.Repository repo : repositories) {
            LOG.info(repo.name);
        }

        LOG.info("Fetching list of the contributors for {}", owner);
        List<String> contributors = github.contributors(owner);
        for (String contributor : contributors) {
            LOG.info(contributor);
        }

        LOG.info("Generating request error");
        try {
            github.contributors(owner, "some-unknown-project");
        } catch (Exception err) {
            LOG.error(err.getMessage());
        }
    }

}
