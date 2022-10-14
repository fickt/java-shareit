package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(RequestDto requestDto, Long requestorId) {
       return post("", requestorId, requestDto);
    }

    public ResponseEntity<Object> getRequestById(Long requestId, Long requestorId) {
        return get("/" + requestId, requestorId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getRangeOfRequests(Long from, Long size, Long userId) {
        Map<String, Object> parameters = Map.of(
          "from", from,
          "size", size
        );
        return get("/all", userId, parameters);
    }
}
