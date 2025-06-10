package dev.amble.ait.core.likes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Identifier;

public class OpinionCache extends ArrayList<Opinion> {
    private boolean valid;

    public OpinionCache() {
        this.valid = false;
    }

    public void invalidate() {
        this.valid = false;
    }
    public void validate(List<Identifier> ids) {
        if (this.size() != ids.size()) this.invalidate();

        this.update(ids);
    }

    public void update(List<Identifier> ids) {
        if (this.valid) return;

        this.clear();
        for (Identifier id : ids) {
            Opinion opinion = Opinion.find(id).orElseThrow();
            this.add(opinion);
        }

        this.valid = true;
    }
}
