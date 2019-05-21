package com.reactor.proxy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletResponse;


import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;


@RestController
public class ProxyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyController.class);
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String HOST = "https://a.tile.openstreetmap.org/";
    private static final String CLIENT = "http://localhost:8080";
    private final CacheService cacheService;


    public ProxyController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

   //TODO: хотелось бы еще улучшить за счет регулярных выражений в маппингах webflux'а
    @GetMapping("/{variable1}/{variable2}/{png}")
    public ResponseEntity<byte[]> getPageWithTile(@PathVariable String variable1, @PathVariable String variable2,
                                                  @PathVariable String png, HttpServletResponse response)
            throws Exception {


        String url = HOST + variable1 + "/" + variable2 + "/" + png;

        File file = new File("cache" + File.separator + url);
        if (file.exists()) {
            return formResponse(cacheService.fromCacheToNet(file), variable1, variable2, png);
        } else {
            return createCash(url);
        }

    }

    //https://stackoverflow.com/questions/21331546/how-to-return-the-image-as-responseentitybyte-and-display-same-using-thyme-l
    protected ResponseEntity<byte[]> formResponse(byte[] fromCacheToNet, String variable1, String variable2, String png) throws URISyntaxException {
        LOGGER.info("Returned from cache");
        new RequestEntity(HttpMethod.GET, new URI(CLIENT + variable1 + "/" + variable2 + "/" + png));
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(fromCacheToNet, headers, HttpStatus.CREATED);
    }

    protected ResponseEntity<byte[]> createCash(String redirectURL) throws Exception {
        RequestEntity requestEntity = new RequestEntity(HttpMethod.GET, new URI(redirectURL));
        ResponseEntity<byte[]> responseEntity;
        try {
            responseEntity = REST_TEMPLATE.exchange(requestEntity, byte[].class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Ooops: there is no such a page!");
        }

        cacheService.fromNetToCache(redirectURL,
                Flux.just(new DefaultDataBufferFactory().wrap(Objects.requireNonNull(responseEntity.getBody()))));
        LOGGER.info("Cache created");

        return responseEntity;
    }

}




