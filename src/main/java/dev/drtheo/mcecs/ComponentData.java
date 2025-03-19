package dev.drtheo.mcecs;

import java.util.ArrayList;

// <=> map<component id, map<entity id, component>>
public class ComponentData extends ArrayList<EntityComponentSet> {

    private final int maxEntities;

    public ComponentData(int maxEntities, int maxComponents) {
        super(maxComponents);

        this.maxEntities = maxEntities;
    }

    public void addComp(ComponentRegistry registry, MEntity entity, MComponent<?> comp) {
        int compId = comp.getUid().get(registry);

        EntityComponentSet data;

        if (compId >= this.size()) {
            for (int i = this.size(); i < compId + 1; i++) {
                super.add(null);
            }

            data = new EntityComponentSet(this.maxEntities);
            super.set(compId, data);
        } else {
            data = super.get(compId);

            if (data == null) {
                data = new EntityComponentSet(this.maxEntities);
                super.set(compId, data);
            }
        }

        data.add(entity.index(), comp);
    }

    public <C extends MComponent<C>> C getComp(ComponentRegistry registry, MEntity entity, CompUid<C> comp) {
        return (C) super.get(comp.get(registry)).get(entity.index());
    }
}
