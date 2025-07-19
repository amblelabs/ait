package dev.amble.ait.core.tardis.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Identifier;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.core.likes.ItemOpinionRegistry;
import dev.amble.ait.core.likes.Opinion;
import dev.amble.ait.core.likes.OpinionCache;
import dev.amble.ait.data.Exclude;

public class OpinionHandler extends KeyedTardisComponent {
    private static final int MAX_ITEM_LIKES = 4;
    private final ArrayList<Identifier> opinions;
    @Exclude
    private OpinionCache cache;

    public OpinionHandler(ArrayList<Opinion> likes) {
        super(Id.OPINION);

        this.opinions = new ArrayList<>(likes.stream().map(Opinion::id).toList());
    }
    public OpinionHandler() {
        this(new ArrayList<>());
    }
    private void formOpinions() {
        for (int i = 0; i < MAX_ITEM_LIKES; i++) {
            Opinion opinion;
            do {
                opinion = ItemOpinionRegistry.getInstance().getRandom();
            } while (this.opinions.contains(opinion.id()));
            this.add(opinion);
        }
    }
    private boolean add(Opinion opinion) {
        if (this.opinions.contains(opinion.id())) return false;

        this.opinions.add(opinion.id());
        return true;
    }

    public boolean contains(Opinion opinion) {
        if (this.opinions.isEmpty()) this.formOpinions();

        return this.opinions.contains(opinion.id());
    }

    /**
     * @return a cached list of opinions, modifying this list wont be permanent as it gets invalidated
     */
    public List<Opinion> opinions() {
        if (this.opinions.isEmpty()) this.formOpinions();

        if (this.cache == null) {
            this.cache = new OpinionCache();
        }

        this.cache.validate(this.opinions);
        return this.cache;
    }
}
