# Science theme searcher
Parser for ELibrary Database

This project is aimed to simplify theme-search in different knowledge bases, such as [elibrary](https://elibrary.ru/defaultx.asp).

You may set some theme as input string and output will contain most famous authors from different author groups. 
*Attention:* search requires some limitations, because of elibrary parse limitations.


# Запуск (требует предварительной настройки базы)

java -jar STS.jar 
-keyword "социоинженерные атаки" 
-parser false 
-searchLimit 20 
-searchLevel 1 
-authorsSynonymy false 
-affiliationsSynonymy false 
-clustererNew false 
-resultType none


keyword - строка, ключевое слово, по которому будет осуществляться поиск

parser - true/false, отвечает за включение/отключения модуля получения данных с eLibrary.ru

searchLimit - число, отвечает за количество сущностей на каждом уровне поиска (по умолчанию - 20)
searchLevel - число, число, отвечает за количество уровней поиска (по умолчанию - 1)
authorsSynonymy - true/false, отвечает за включение/отключения модуля поиска синонимичных авторов и их удаление из БД
affiliationsSynonymy - true/false, отвечает за включение/отключения модуля поиска синонимичных аффилиаций и их удаление из БД
clustererNew - true/false, отвечает за включение/отключения модуля кластеризации (разбиение графа на группы соавторов и представление итогового графа)
resultType - none/metric/year, отвечает за включение/отключения модуля нахождения итогового списка публикаций с учетом их сортировки по цитированию (metric) или годам (year)

Пример запуска (в папке с запускаемым файлом STS.jar):
java -jar STS.jar -keyword "социоинженерные атаки" -parser false -searchLimit 20 -searchLevel 1 -authorsSynonymy false -affiliationsSynonymy false -clustererNew false -resultType none

Файл запуска находится по ссылке:
https://drive.google.com/file/d/1Kc-V5Lr67EDrDgIB512_XFGvPfV5yDbL/view?usp=sharing
