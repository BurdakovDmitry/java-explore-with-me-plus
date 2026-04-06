JAVA-EXPLORE-WITH-ME-PLUS - групповой проект

Реализация фичи Comments:

ADMIN: (Предлагаю AdminCommentController в директории сomment)
"GET/admin/comments" - Поиск комментариев по параметрам 
#text - текст для поиска в содержимом комментарии, 
#List<Long> User - поиск по списку авторов комментариев,
#List<Long> Event - поиск по списку событий,
#Enum status - поиск по статусу комментария (опубликован или нет),
#LocalDateTime rangeStart - дата и время не раньше которых оставлены комментарии,
#LocalDateTime rangeEnd - дата и время не позже которых оставлены комментарии,
#Integer from - количество комментарий, которые нужно пропустить для формирования текущего набора,
#Integer size - количество комментарий в наборе

"PATCH/admin/comments/{commentsId}" - обновление статуса комментария - опубликован или отклонен

"DELETE/admin/comments/{commentsId}" - удалить любой опубликованный комментарий

-------------------------------------------------------------------------

PUBLIC: (Предлагаю CommentController в директории сomment)
"GET/comments" - Поиск опубликованных комментариев по параметрам
#text - Поиск по слову(части слова),
#List<Long> Event - поиск по списку событий,
#LocalDateTime rangeStart - дата и время не раньше которых оставлены комментарии,
#LocalDateTime rangeEnd - дата и время не позже которых оставлены комментарии,
#Integer from - количество комментарий, которые нужно пропустить для формирования текущего набора,
#Integer size - количество комментарий в наборе
#String sort - сортировка по дате

"GET/comments/{commentsId}" - поиск опубликованного комментария

-------------------------------------------------------------------------

PRIVATE: (Предлагаю PrivateCommentController в директории user)
"GET/users/{userId}/comments" - получение всех опубликованных комментариев пользователя

"GET/users/{userId}/events/{eventId}/comments" - получение всех опубликованных комментариев пользователя по событию

"POST/users/{userId}/events/{eventId}/comments" - добавление комментария пользователем после посещения события

"GET/users/{userId}/events/{eventId}/comments/{commentsId}" - получение своего комментария

"PATCH/users/{userId}/events/{eventId}/comments/{commentsId}" - обновление своего комментария

"DELETE/users/{userId}/events/{eventId}/comments/{commentsId}" - удаление своего комментария