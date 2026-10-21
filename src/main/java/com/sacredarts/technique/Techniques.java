package com.sacredarts.technique;

import com.sacredarts.path.Path;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Registry of every Path's four techniques plus its ultimate. */
public class Techniques {
    private static final Map<Path, List<Technique>> BY_PATH = new EnumMap<>(Path.class);
    private static final Map<Path, Technique> ULTIMATES = new EnumMap<>(Path.class);

    static {
        register(Path.BLACK_FLAME, BlackFlameTechniques.create(), BlackFlameTechniques.ultimate());
        register(Path.TWIN_STARS, TwinStarsTechniques.create(), TwinStarsTechniques.ultimate());
        register(Path.ENDLESS_SWORD, EndlessSwordTechniques.create(), EndlessSwordTechniques.ultimate());
        register(Path.HOLLOW_KING, HollowKingTechniques.create(), HollowKingTechniques.ultimate());
        register(Path.WHITE_FOX, WhiteFoxTechniques.create(), WhiteFoxTechniques.ultimate());
        register(Path.SANDVIPER, SandviperTechniques.create(), SandviperTechniques.ultimate());
    }

    private static void register(Path path, List<Technique> techniques, Technique ultimate) {
        BY_PATH.put(path, List.copyOf(techniques));
        ULTIMATES.put(path, ultimate);
    }

    /** The four techniques, in keybind slot order: Striker, Enforcer, Ruler, Forger. */
    public static List<Technique> forPath(Path path) {
        return BY_PATH.get(path);
    }

    public static Technique ultimateFor(Path path) {
        return ULTIMATES.get(path);
    }
}
