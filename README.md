# rutor-parser

Выдает результаты поиска с первой страницы в формате JSON.

AdoptOpenJDK 14.0.2

## Сборка
```
./gradlew build
```

## Собрать образ (выполняется после ручной сборки jar)

```
docker build -t rutor_parser:2.0.0 .
```

## Запуск
```
docker run -d -p 8080:8080 --restart always --name rutor_parser rutor_parser:2.0.0
```

## Остановка
```
docker stop rutor_parser
```

## Удалить контейнер
```
docker rm rutor_parser
```
