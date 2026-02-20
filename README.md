# Kobweb Site

Проект переведён на архитектуру как в демо `kotlin-kobweb-demo`. Основной модуль – `site/`, это Kobweb-приложение на Compose for Web.

## Демо верстки

В модуле `site` доступны демо-страницы по маршруту `/demo` — верстка с фейковыми данными (ProfileBlock, Layout, карточки). Файлы в `site/src/jsMain/kotlin/.../pages/demo/`.

**Почему не отдельный модуль?** Kobweb + Compose (1.5.x) при двух application-модулях в одном репо дают ошибку `BuildServiceParameters`. Отдельный модуль `siteDemo` потребовал бы обновления стека (Compose 1.6+, Kobweb новее) или рефакторинга на shared library.

## Запуск dev-сервера

После сборки приложение будет доступно на `http://localhost:8080` с live reload. Остановить сервер можно в терминале клавишей `Q`.

## Сервер API

- **IP сервера:** 155.212.162.11  
- **URL API:** https://155.212.162.11  
- **Swagger (Onyx Backend):** http://155.212.162.11:3000/api/docs  

Базовый URL задаётся в настройках Ktor (см. `ApiConfig` в коде).

## Экспорт статики

```bash
cd site
kobweb export --layout static
```

Готовую статику можно разместить на GitHub Pages / Netlify и т.п.