# Каталог відео (`videos.json`)

## Формат дати `published_at`

Використовується **SQL datetime** (MySQL/SQLite): `YYYY-MM-DD HH:MM:SS`, наприклад `2025-05-15 10:00:00`.

У додатку можна парсити через `LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))`.

## `sort_order` — порядок відображення

`sort_order` — **ціле число для ручного (кураторського) порядку**. Менше значення = вище у списку.

| Режим сортування в додатку | Логіка |
|----------------------------|--------|
| **`RANDOM`** (за замовчуванням, як зараз) | Список перемішується один раз за сесію (`shuffled()`). Поле `sort_order` **ігнорується**. |
| **`SORT_ORDER_ASC`** | `videos.sortedBy { it.sortOrder }` — порядок з JSON/CMS. |
| **`SORT_ORDER_DESC`** | `videos.sortedByDescending { it.sortOrder }` — «важливі» з більшим числом зверху. |
| **`PUBLISHED_AT_DESC`** | Новіші спочатку (`published_at` спадаючи). |
| **`PUBLISHED_AT_ASC`** | Старіші спочатку. |

Рекомендація для CMS: крок **100** між відео (`100`, `200`, …), щоб можна було вставити нове між існуючими (`150`) без перенумерації всіх.

## Інші поля

| Поле | Опис |
|------|------|
| `is_premium` | Потрібна підписка Imio Premium |
| `is_bedtime` | Показується у вечірньому режимі |
| `description` | Короткий опис для екрана відео |
| `categories` | Теги категорій (`animals`, `vehicles`, …) |
| `search_keywords` | Додаткові слова для пошуку |
| `related_video_ids` | ID схожих відео для related-блоку |
| `age_min` / `age_max` | Вікова аудиторія (зараз 2–6) |

Файл-еталон для завантаження на сервер: [`videos.json`](./videos.json).
