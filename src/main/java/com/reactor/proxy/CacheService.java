package com.reactor.proxy;


import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.io.*;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
public class CacheService {


//Сервер должен быть способен работать в многопоточной среде. Операции над потоковыми данными
// должны осуществляться через Flux-API Reactor-а. Операции ввода-вывода должны быть стараться быть асинхронными.

    protected void fromNetToCache(String url, Flux<DataBuffer> dataBufferFlux) throws Exception {

       String[] strings = url.split("/");

        File f = new File(new File("cache" + File.separator + url).getParent());
        if (!f.exists()) f.mkdirs();

        File file = new File( f.getAbsolutePath() + File.separator + strings[strings.length - 1]);
        file.createNewFile();
        Path path = file.toPath();


        AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);
        Mono<Path> mono = DataBufferUtils.write(dataBufferFlux, channel)
                .map(DataBufferUtils::release)
                .then(Mono.just(path));

        mono.subscribe();

    }


    protected byte[] fromCacheToNet(File file) throws Exception {

        FileInputStream fis = new FileInputStream(file);

        byte[] bytes = new byte[FileCopyUtils.BUFFER_SIZE];
        int r = 1;
        while (r > 0) {
            r = fis.read(bytes);
        }
        return bytes;
    }
}