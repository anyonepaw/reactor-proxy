При помощи фреймворка Reactor https://projectreactor.io разработать кеширующий веб-прокси-сервер к тайлам(частям карты) с сайта https://www.openstreetmap.org/.

То есть, например, при обращении к http://localhost:8080/4/6/6.png запрос должен перенаправляться к https://a.tile.openstreetmap.org/4/6/6.png, ответ должен кешироваться на диске прокси-сервера, а все последующие запросы по тому же URL не должны больше перенаправляться к https://www.openstreetmap.org/, а браться из сохраненной копии.

Если два клиента прокси-сервера запрашивают одновременно один и тот же тайл, то к https://www.openstreetmap.org/ должен отправиться только один запрос.

Если запрос еще не был закеширован, то запись ответа на диск должна осуществляться параллельно с ответом клиенту.

Сервер должен быть способен работать в многопоточной среде. Операции над потоковыми данными должны осуществляться через Flux-API Reactor-а. Операции ввода-вывода должны быть стараться быть асинхронными.

При оценке решения будет уделяться внимание качеству кода: код должен быть понятным и легко модифицируемым (возможно, в ущерб лаконичности и даже производительности). Код должен быть покрыт тестами, и качество кода тестов также оценивается.

Вы не ограничены в выборе сторонних библиотек, кроме принудительного использования https://projectreactor.io. И, разумеется, не нужно писать свой http-сервер или клиент с нуля. В качестве языка программирования можно использовать Java или Kotlin.

Если вы не можете или не хотите выполнять некоторые из требований - вы все равно можете отправить решение, но, пожалуйста, укажите что вы осознанно опустили некоторые требования, чтобы это не было рассмотрено как ошибка.

Ответ принимается в виде ссылки на github/bitbucket (можете добавить пользователя xiexed/NicolayMitropolsky если не хотите делать репозиторий публичным).