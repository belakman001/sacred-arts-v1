package com.sacredarts.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * "Welcome to Sacred Arts" — the guide book every player spawns with.
 * A plain vanilla written book (no new item, model or texture needed);
 * the content is the mod's own words, written from the code that runs.
 * Given once per player, on first login, via a persistent flag.
 */
public final class GuideBook {
    public static final String TITLE = "Welcome to Sacred Arts";
    public static final String AUTHOR = "Sacred Arts";
    private static final String GIVEN_TAG = "sacredarts_guide_given";

    /** Pages are kept short (under ~250 chars) so they fit the book UI. */
    private static final String[] PAGES = {
        "Welcome to Sacred Arts\nThe Way opens before you. This book holds what every new sacred artist must know: the Paths, the stages of advancement, and the aura that breathes through the world. Keep it close.",
        "Beginning Your Path\nFind a Foundation Manual. One rests in the chiseled bookshelf of every Abandoned Shrine, deep in the forests. Hold it and use it to choose your Path. A Remnant Core can change your Path later.",
        "The Six Paths\nBlack Flame - Fire, Destruction. Twin Stars - Light, Force. Endless Sword - Sword, Force. Hollow King - Shadow, Force. White Fox - Light, Dream. Sandviper - Blood, Life.",
        "Cycling Madra\nHold C to cycle. Cycling restores madra and builds your foundation. Cycle where your Path's aura lives and you will also gather vital aura, the gold bar beside your madra. Press K to see your progress.",
        "Stages of Advancement\nFoundation: cycle 500 madra to reach Copper. Copper: defeat 20 foes to reach Iron. Iron: gain 25 mastery to reach Jade. Mastery grows when you cast techniques and defeat foes.",
        "Stages of Advancement II\nJade: 50 mastery and 5,000 madra cycled to reach Lowgold. Lowgold: 100 mastery to reach Highgold. Highgold: 150 mastery to reach Truegold. The Lord realm lies beyond.",
        "What Each Stage Grants\nEach stage raises your madra and your level cap. Copper opens Copper Sight (G). Iron opens your Iron Body (F). Jade opens Jade Sense (J). At Lowgold your Goldsign appears.",
        "Madra by Stage\nFoundation 20, Copper 40, Iron 60, Jade 80, Lowgold 110, Highgold 140, Truegold 180. Your level cap grows with you: 5, 10, 20, 25, 35, 60, 100.",
        "Sight and Sense\nCopper Sight (G) shows the aura drifting in the air and the aura in ores below. Hostile creatures glow through walls. Jade Sense (J) shows every living thing: red for hostile, green for peaceful. Look at a creature to read its spirit.",
        "What Is Aura?\nAura is the vital essence of the world. It drifts in the air, lives in the land, and answers to what happens around you. Cycle where your Path's aura is strong and you will gather it.",
        "Aura Colors\nFire is orange: deserts, badlands, the Nether. Water is blue: oceans, rivers, swamps, snow, beaches. Earth is brown: mountains, badlands, the deep dark, below Y 0. Wind is pale: mountains, snow, beaches, plains.",
        "Aura Colors II\nLife is green: jungles, forests, mushroom fields, plains. Shadow is purple: the deep dark, below Y 0, the Nether, the End. Blood is dark red: swamps. Force is violet: mountains, the End.",
        "Aura Colors III\nDream is pink: jungles, cherry groves, mushroom fields. Light is gold: deserts. Destruction is dark red: the Nether, and where things explode. Sword is silver: near blades, stronger with better swords.",
        "Aura in the Moment\nAura also answers to events. Blood rises when you strike a foe. Dream gathers in villages at night. Wind lives high above Y 120. Destruction flares where things explode. A sword in hand or on the ground breathes Sword aura.",
        "Keys of the Way\nV, B, N, M: techniques. Sneak + M: your ultimate. Hold C: cycle. K: your progress. G: Copper Sight. J: Jade Sense. F: Iron Body. Walk the Way well, artist."
    };

    private GuideBook() {
    }

    /** The finished book, as a vanilla written-book stack. */
    public static ItemStack create() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag tag = new CompoundTag();
        tag.putString("title", TITLE);
        tag.putString("author", AUTHOR);
        ListTag pages = new ListTag();
        for (String page : PAGES) {
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(page))));
        }
        tag.put("pages", pages);
        book.setTag(tag);
        return book;
    }

    /** Give the book once, on first login. Existing players get it on next login. */
    public static void giveIfFirstLogin(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (data.getBoolean(GIVEN_TAG)) return;
        data.putBoolean(GIVEN_TAG, true);
        ItemHandlerHelper.giveItemToPlayer(player, create());
    }
}
