# EldoriaItems - Custom Item System

Drop the `src/main/java/me/chaos/EldoriaItems` folder into your existing `src/main/java/me/chaos/`
directory. It's a self-contained module - it only depends on Paper API, Adventure, Gson and
Mojang's `Codec` (all already in your `build.gradle.kts`).

## 1. Wiring it into Main.java

Add one line to `Main#onEnable()`, **before** any code that registers your own items
(e.g. before an `ItemLoader.loadItems()` call, mirroring how `QuestLoader.loadQuests()` works):

```java
@Override
public void onEnable() {
    coreApi = MultiverseCoreApi.get();

    me.chaos.EldoriaItems.EldoriaItemsModule.init(this); // <-- add this

Included is some information about a GitHub repository and its language composition.
repo: GalaxiesChaos/Eldoria
repo ID: 1312296690
