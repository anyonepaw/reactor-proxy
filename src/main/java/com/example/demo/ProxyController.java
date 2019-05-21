package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTML;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;

import java.util.Enumeration;
import java.util.List;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

//разработать кеширующий веб-прокси-сервер к тайлам(частям карты) с сайта https://www.openstreetmap.org/.
@RestController
public class ProxyController {

    private final CacheService cacheService;

    public ProxyController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    RestTemplate restTemplate = new RestTemplate();
//Если два клиента прокси-сервера запрашивают одновременно один и тот же тайл,
// то к https://www.openstreetmap.org/ должен отправиться только один запрос.
//

//Сервер должен быть способен работать в многопоточной среде. Операции над потоковыми данными
// должны осуществляться через Flux-API Reactor-а. Операции ввода-вывода должны быть стараться быть асинхронными.
//
//    @GetMapping("/{name:[a-z-]+}-{version:\\d\\.\\d\\.\\d}{ext:\\.[a-z]+}")
//    public void handle(@PathVariable String version, @PathVariable String ext) {
//        // ...
//    }

    @GetMapping("/{variable1}/{variable2}/{png}")
    public ResponseEntity<byte[]> getPageWithTile(@PathVariable String variable1, @PathVariable String variable2,
                                                  @PathVariable String png, HttpServletResponse response)
            throws Exception {

        String redirectURL = "https://a.tile.openstreetmap.org/" + variable1 + "/" + variable2 + "/" + png;

        if ((new File("cache" + File.separator + redirectURL)).exists()) {
            byte[] bytes = cacheService.returnFileFromCache("cache" + File.separator + redirectURL);
            RequestEntity requestEntity = new RequestEntity(HttpMethod.GET, new URI("http://localhost:8080/4/6/6.png"));
            //https://stackoverflow.com/questions/21331546/how-to-return-the-image-as-responseentitybyte-and-display-same-using-thyme-l
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(bytes, headers, HttpStatus.CREATED);
        } else {
            //Если запрос еще не был закеширован, то запись ответа на диск должна
            // осуществляться параллельно с ответом клиенту.
            RequestEntity requestEntity = new RequestEntity(HttpMethod.GET, new URI(redirectURL));
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
            byte[] body = responseEntity.getBody();
            System.out.println(responseEntity.getStatusCode());


            // List<HttpHeaders> httpHeaders = request.getHeaders();

            cacheService.fromNetToCache(redirectURL, body);

//            ResponseEntity<byte[]> responseEntity = restTemplate
//                        .exchange(result.getBody().getUrl(),
//                                HttpMethod.GET,
//                                HttpEntity.EMPTY,
//                                byte[].class);
//
//                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//                assertThat(responseEntity.getBody()).isEqualTo(readAllBytes(get(dukeResource.getURI())));
//
//        }

            response.sendRedirect(redirectURL);
            return new ResponseEntity<>(HttpStatus.OK);

        }


        //        }

//        RequestCallback requestCallback = new RequestCallback() {
//            @Override
//            public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
//                clientHttpRequest.execute();
//            }
//        };
        //  RequestEntity requestEntity = restTemplate.getForEntity(redirectURL, )
// обработать employees


//final Mono<T> savedEntity = repository.save(entity)
//    .doOnNext(entity -> entityCacheService.putIntoCache(entity.getId(), entity);


        // Mono.fromCallable(() -> cacheService.decideWhatToDoWithFILe(redirectURL)).

//        final Mono<T> savedEntity = cacheService.fromNetToCache("jk","kkl", 80, "kl")
//
//    .doOnNext(entity -> entityCacheService.putIntoCache(entity.getId(), entity);
//


//         cacheService.from_net();

//        Mono<String> person =
//                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body();


        //  return ServerResponse.BodyBuilder("https://a.tile.openstreetmap.org/" + variable1 + "/" + variable2 + "/" + png);

//        public Flux<String> uploadHandler(@RequestBody Flux<Part> parts) {
//            return parts
//                    .filter(part -> part instanceof FilePart) // only retain file parts
//                    .ofType(FilePart.class) // convert the flux to FilePart
//                    .flatMap(this::saveFile); // save each file and flatmap it to a flux of results
//        }

    }
}



