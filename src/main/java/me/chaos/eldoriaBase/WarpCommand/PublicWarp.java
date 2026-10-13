package me.chaos.eldoriaBase.WarpCommand;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.chaos.eldoriaBase.Utils.DataHolder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PublicWarp implements DataHolder {
    // FIXED: was HashSet<WarpPoint>, but WarpPoint has no equals/hashCode, so the
    // "set" never actually deduplicated. A map keyed by lower-case id does.
    private final Map<String, WarpPoint> publicWarps = new LinkedHashMap<>();

    public List<WarpPoint> getPublicWarps() {
        return List.copyOf(publicWarps.values());
    }

    public void add(WarpPoint point) {
        publicWarps.put(point.getId().toLowerCase(Locale.ROOT), point);
    }

    public void SaveToFile() {
        WriteConfig(CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow());
    }

    public void loadFromFile() {
        publicWarps.clear();
        CODEC.parse(JsonOps.INSTANCE, DataHolder.ReadConfig())
                .resultOrPartial(System.err::println)
                .ifPresent(loaded -> loaded.getPublicWarps().forEach(this::add));
    }

    public PublicWarp() {
        loadFromFile();
    }

    private PublicWarp(List<WarpPoint> warps) {
        warps.forEach(this::add);
    }

    public static Codec<PublicWarp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WarpPoint.CODEC.listOf().fieldOf("Warps").forGetter(PublicWarp::getPublicWarps)
    ).apply(instance, PublicWarp::new));
}
