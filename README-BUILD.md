# Что здесь и откуда

## Подтверждено (вытащено из META-INF/MANIFEST.MF твоих же jar-файлов, не угадано)
- Minecraft `1.21.11`
- Fabric Loader `0.18.2`
- Fabric Loom `1.14.10`
- Gradle `9.2.0`
- Маппинги — `intermediary` (НЕ Yarn — отсюда в коде `class_310`, `method_36456` и т.п.
  вместо `MinecraftClient`/`setYaw`). `build.gradle` настроен соответственно.

## Предположение, не проверено (сверь перед первым билдом)
- Версия `fabric-api` в `gradle.properties` (`fabric_version`) — её нет и не может
  быть в манифесте собранного мода, я не могу получить актуальную версию под
  1.21.11 без доступа в сеть. Проверь на https://fabricmc.net/develop/ и поправь.

## Чего не хватает и почему
- **`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`** — последний
  это бинарник, скачиваемый с серверов Gradle; у меня нет сети, чтобы его
  получить. Поэтому workflow (`.github/workflows/build.yml`) собирает через
  `gradle/actions/setup-gradle` (ставит Gradle 9.2.0 сам) и вызывает `gradle build`
  напрямую — тебе не нужен `./gradlew`, чтобы собрать джарник на GitHub.
  Если всё же нужен `./gradlew` для локальной сборки — один раз запусти
  `gradle wrapper --gradle-version 9.2.0` на машине с установленным Gradle
  (или добавь шаг `gradle wrapper` в начало workflow и закоммить результат) —
  это сгенерирует оба скрипта и .jar автоматически.
- **`LICENSE`** — не было в выгрузке, не добавлял.

## Структура
```
src/main/java/ru/white/...       — весь код (3 файла пропатчены под плавную камеру)
src/main/resources/fabric.mod.json, mixins.json
build.gradle, settings.gradle, gradle.properties
.github/workflows/build.yml      — собирает build/libs/*.jar как артефакт Actions
```

Запушишь в `derbentweb/white-dlc` (или любой свой репозиторий) — на любой push
или вручную через "Run workflow" соберётся jar, скачать из вкладки Actions →
конкретный run → Artifacts → `white-jar`.
