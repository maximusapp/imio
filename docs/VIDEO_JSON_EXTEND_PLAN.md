Ось практичний розбір з урахуванням того, як Imio уже працює: HLS, premium, isBedtime для вечірнього режиму, пошук лише по title, related = «всі інші з кешу», порядок на Home — shuffle на клієнті.

1. Спочатку — конвенції (важливо зараз)
   У JSON змішані стилі: manifest_url (snake) і isPremium (camel). Краще одразу все в snake_case і не міняти назви полів потім:

"is_premium": true,
"is_bedtime": false
На рівні відповіді:

{
"schema_version": 1,
"generated_at": "2026-06-02T12:00:00Z",
"videos": [ ... ]
}
schema_version — щоб додавати поля без ламання старих збірок.

2. Високий пріоритет (майже точно знадобляться)
   Поле	Навіщо
   status (published / hidden / draft)
   Приховати відео без видалення з JSON
   sort_order або weight
   Керований порядок на Home замість random shuffle
   published_at / updated_at
   Бейдж «Нове», сортування, інвалідація кешу
   description (короткий)
   Екран відео, пошук, SEO в Store
   related_video_ids
   Related як у YouTube, а не «всі крім поточного»
   tags або categories
   Фільтри (тварини, транспорт, колискові…) — зараз лише ALL/PREMIUM
   age_min / age_max (напр. 2–6)
   Батьківський режим, фільтрація контенту
   search_keywords
   Пошук краще за один title («котик», «пожежник»)
   Для вечірнього режиму вже є is_bedtime; можна додати mood (calm, active) — гнучкіше за один boolean.

3. Медіа та плеєр (коли розширюватимете відео)
   Поле	Навіщо
   poster_image
   Велике прев’ю на деталях (окремо від preview_image у списку)
   preview_image_small
   Thumbnail у LazyRow landscape — менше трафіку
   subtitles
   [{ "locale": "uk", "url": "...vtt", "default": true }] — доступність
   audio_tracks
   Якщо з’являться різні мови озвучки
   aspect_ratio
   Коректний layout (16:9 vs 4:3)
   has_drm / license_type
   Якщо колись підключите не WebDAV, а ліцензований CDN
   format: "hls" залишити, але можна stream_type: hls / dash / mp4 на майбутнє.

4. Локалізація (якщо плануєте EN / кілька мов)
   Зараз locale на відео — одна мова на запис. Краще:

"default_locale": "uk",
"localizations": [
{
"locale": "uk",
"title": "Тигр із савани",
"description": "..."
},
{
"locale": "en",
"title": "Tiger from the savanna",
"description": "..."
}
]
Тоді один id — одне відео, багато мов (і Room/favorites не дублюються).

5. Серії та плейлисти
   Поле	Навіщо
   series_id
   «Пригоди грузовичків» — кілька серій
   episode_number
   Порядок у серії
   playlist_ids
   Підбірки з бекенду (вечірні, безкоштовні тижня)
6. Бізнес / доступність контенту
   Поле	Навіщо
   requires_premium
   Те саме що is_premium, але явніше в API
   free_preview_seconds
   Freemium: 30 с без підписки
   available_from / available_until
   Акції, сезонний контент
   min_app_version
   Не показувати нове відео на старих APK
7. Аналітика та адмінка (опційно, nullable)
   Поле	Навіщо
   analytics_id
   Стабільний ID для Firebase/Amplitude при зміні manifest_url
   view_count
   «Популярне» (якщо рахуєте на сервері)
   author / studio
   Кредити, довіра батьків
   content_id
   Зовнішній ID з CMS
8. Що можна не дублювати
   duration_ms — достатньо, якщо не плануєте глави в середині ролика.
   format — якщо завжди HLS, можна лишити, але реально використовується manifest_url.
   Окремий locale на кожному відео — замінити на localizations, якщо буде >1 мови.
   Приклад «розширеного» об’єкта (додати зараз, nullable)
   {
   "id": "16",
   "status": "published",
   "sort_order": 100,
   "published_at": "2026-01-15T10:00:00Z",
   "updated_at": "2026-06-01T08:00:00Z",
   "title": "Тигр із савани",
   "description": "Спокійна історія про тигреня.",
   "search_keywords": ["тигр", "савана", "тварини"],
   "format": "hls",
   "manifest_url": "https://...",
   "duration_ms": 211800,
   "aspect_ratio": "16:9",
   "preview_image": "https://...",
   "poster_image": null,
   "preview_image_small": null,
   "default_locale": "uk",
   "localizations": [],
   "is_premium": true,
   "is_bedtime": false,
   "mood": "calm",
   "age_min": 2,
   "age_max": 6,
   "tags": ["animals", "bedtime-friendly"],
   "categories": ["animals"],
   "series_id": null,
   "episode_number": null,
   "related_video_ids": ["15", "14"],
   "subtitles": [],
   "analytics_id": "video_16",
   "min_app_version": null
   }
   У Gson/Kotlin — nullable + default (= null, = emptyList()), щоб старий JSON без полів не падав.

Що б я додав першим чергою саме для Imio
schema_version + status + sort_order + updated_at
description + search_keywords + tags
related_video_ids
age_min / age_max (або один age_rating: "2-6")
Уніфікація is_premium / is_bedtime у snake_case
Заготовка localizations (порожній масив зараз)
subtitles / poster_image — порожні, якщо ще немає файлів
Це покриває пошук, фільтри, related, вечірній режим, premium, майбутні мови й плеєр — без over-engineering.

Якщо скажеш, що плануєш у найближчі 3–6 місяців (серії, EN, субтитри, рекомендації), можу звузити список до мінімального «must have» під твій roadmap. У Agent mode можу оновити VideoDto, Room і маппер під обраний набір полів.