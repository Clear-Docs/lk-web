# Kobweb Site

Проект переведён на архитектуру как в демо `kotlin-kobweb-demo`. Основной модуль – `site/`, это Kobweb-приложение на Compose for Web.

## Запуск dev-сервера

```bash
cd site
kobweb run
```

После сборки приложение будет доступно на `http://localhost:8080` с live reload. Остановить сервер можно в терминале клавишей `Q`.

## Экспорт статики

```bash
cd site
kobweb export --layout static
```

Готовую статику можно разместить на GitHub Pages / Netlify и т.п.