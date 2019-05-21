package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;

@Service
public class CacheService {

    Logger LOGGER = LoggerFactory.getLogger(CacheService.class);


    //https://github.com/entzik/reactive-spring-boot-examples/blob/master/src/main/java/com/thekirschners/springbootsamples/reactiveupload/ReactiveUploadResource.java
    //https://habr.com/en/company/piter/blog/435972/
    // загружает из сети страничку с одновременным кэшированием её на диск
    // странички в кэше хранятся прямо с HTTP заголовком
    protected void fromNetToCache(String url, byte[] body) throws Exception {

        String path = "cache" + File.separator + url;

        File file = new File((new File(path)).getParent());
        if (!file.exists()) file.mkdirs();

        FileOutputStream fos = new FileOutputStream(path);
        fos.write(body);
        fos.close();

    }


    protected byte[] returnFileFromCache(String path) throws Exception {
        File file = new File(path);
        System.out.println(file.length());
        System.out.println(file.getTotalSpace());
        FileInputStream fis = new FileInputStream(path);

        byte[] bytes = new byte[FileCopyUtils.BUFFER_SIZE];
        int r = 1;
        while (r > 0) {
            r = fis.read(bytes);
        }
        return bytes;
    }


    //https://stackoverflow.com/questions/51695123/how-to-convert-reactor-fluxstring-to-inputstream
//    static InputStream createInputStream(Flux<byte[]> flux) {
//
//        PipedInputStream in = new PipedInputStream();
//        flux.subscribeOn(Schedulers.elastic())
//                .subscribe(new PipedStreamSubscriber(in));
//
//        return in;
//    }

//        try (InputStream in = createInputStream(jedi)) {
//        byte[] data = new byte[5];
//        int size = 0;
//        while ((size = in.read(data)) > 0) {
//            System.out.printf("%s", new String(data, 0, size));
//        }
//    }


//    public Mono<String> storeFile(Enumeration<String> headerNames, String header, String host, int port, String path) throws IOException {
//        Socket sc = new Socket(host, port);
//        InputStream is = sc.getInputStream();
//        int r = is.read(buf);
//
//        return
//
//            return filePart.content()
//                    .reduce(DataBuffer::write).map(DataBuffer::asInputStream)
//                    .map(input -> gridFsTemplate.store(input, filePart.filename(), contentType))
//                    .map(ObjectId::toHexString);
//    }


//
//        @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//        public Flux<String> uploadHandler(@RequestBody Flux<Part> parts) {
//            return parts
//                    .filter(part -> part instanceof FilePart) // only retain file parts
//                    .ofType(FilePart.class) // convert the flux to FilePart
//                    .flatMap(this::saveFile); // save each file and flatmap it to a flux of results
//        }


//    private Mono<String> saveFile(String path) {
//
//
//        // if a file with the same name already exists in a repository, delete and recreate it
//        File file = new File(path);
//        if (file.exists())
//            //пиши
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                return Mono.error(e); // if creating a new file fails return an error
//            }
//
//        try {
//            // create an async file channel to store the file on disk
//            final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.WRITE);
//
//            //final CloseCondition closeCondition = new CloseCondition();
//
//            FilePart
//
//            // pointer
//            // to the end of file offset
//            AtomicInteger fileWriteOffset = new AtomicInteger(0);
//            // error signal
//            AtomicBoolean errorFlag = new AtomicBoolean(false);
//
//            LOGGER.info("subscribing to file parts");
//            // FilePart.content produces a flux of data buffers, each need to be written to the file
//            return filePart.content().doOnEach(dataBufferSignal -> {
//                if (dataBufferSignal.hasValue() && !errorFlag.get()) {
//                    // read data from the incoming data buffer into a file array
//                    DataBuffer dataBuffer = dataBufferSignal.get();
//                    int count = dataBuffer.readableByteCount();
//                    byte[] bytes = new byte[count];
//                    dataBuffer.read(bytes);
//
//                    // create a file channel compatible byte buffer
//                    final ByteBuffer byteBuffer = ByteBuffer.allocate(count);
//                    byteBuffer.put(bytes);
//                    byteBuffer.flip();
//
//                    // get the current write offset and increment by the buffer size
//                    final int filePartOffset = fileWriteOffset.getAndAdd(count);
//                    LOGGER.info("processing file part at offset {}", filePartOffset);
//                    // write the buffer to disk
//                    closeCondition.onTaskSubmitted();
//                    fileChannel.write(byteBuffer, filePartOffset, null, new CompletionHandler<Integer, ByteBuffer>() {
//                        @Override
//                        public void completed(Integer result, ByteBuffer attachment) {
//                            // file part successfuly written to disk, clean up
//                            LOGGER.info("done saving file part {}", filePartOffset);
//                            byteBuffer.clear();
//
//                            if (closeCondition.onTaskCompleted())
//                                try {
//                                    LOGGER.info("closing after last part");
//                                    fileChannel.close();
//                                } catch (IOException ignored) {
//                                    ignored.printStackTrace();
//                                }
//                        }
//
//                        @Override
//                        public void failed(Throwable exc, ByteBuffer attachment) {
//                            // there as an error while writing to disk, set an error flag
//                            errorFlag.set(true);
//                            LOGGER.info("error saving file part {}", filePartOffset);
//                        }
//                    });
//                }
//            }).doOnComplete(() -> {
//                // all done, close the file channel
//                LOGGER.info("done processing file parts");
//                if (closeCondition.canCloseOnComplete())
//                    try {
//                        LOGGER.info("closing after complete");
//                        fileChannel.close();
//                    } catch (IOException ignored) {
//                    }
//
//            }).doOnError(t -> {
//                // ooops there was an error
//                LOGGER.info("error processing file parts");
//                try {
//                    fileChannel.close();
//                } catch (IOException ignored) {
//                }
//                // take last, map to a status string
//            }).last().map(dataBuffer -> filePart.filename() + " " + (errorFlag.get() ? "error" : "uploaded"));
//        } catch (IOException e) {
//            // unable to open the file channel, return an error
//            LOGGER.info("error opening the file channel");
//            return Mono.error(e);
//        }
//    }


}
