package dev.drtheo.mcecs.base.data;

import dev.drtheo.mcecs.base.comp.CompUid;
import dev.drtheo.mcecs.base.comp.ComponentRegistry;
import dev.drtheo.mcecs.base.comp.Component;
import dev.drtheo.mcecs.base.EEntity;

import java.util.ArrayList;
import java.util.function.Consumer;

// <=> map<component id, map<entity id, component>>
public class ComponentData extends ArrayList<EntityComponentSet> {

    private final int maxEntities;

    public ComponentData(int maxEntities, int maxComponents) {
        super(maxComponents);

        this.maxEntities = maxEntities;
    }

    public void addComp(ComponentRegistry registry, EEntity entity, Component<?> comp) {
        int compId = comp.getUid().get(registry);
        comp.setOwner(entity);

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

    public <C extends Component<C>> void getComp(ComponentRegistry registry, CompUid<C> comp, Consumer<C> c, EEntity... entities) {
        EntityComponentSet set = super.get(comp.get(registry));

        for (EEntity entity : entities) {
            c.accept((C) set.get(entity.index()));
        }
    }

    public <C extends Component<C>> C getComp(ComponentRegistry registry, EEntity entity, CompUid<C> comp) {
        return (C) super.get(comp.get(registry)).get(entity.index());
    }

    public <C1 extends Component<C1>,
            C2 extends Component<C2>>
    void fetchComps(ComponentRegistry registry, EEntity entity, CompUid<C1> c1u, CompUid<C2> c2u, T2<C1, C2> t) {
        C1 c1 = (C1) super.get(c1u.get(registry)).get(entity.index());
        C2 c2 = (C2) super.get(c2u.get(registry)).get(entity.index());

        t.get(c1, c2);
    }

    public <C1 extends Component<C1>,
            C2 extends Component<C2>,
            C3 extends Component<C3>>
    void fetchComps(ComponentRegistry registry, EEntity entity, CompUid<C1> c1u, CompUid<C2> c2u, CompUid<C3> c3u, T3<C1, C2, C3> t) {
        int entId = entity.index();

        C1 c1 = (C1) super.get(c1u.get(registry)).get(entId);
        C2 c2 = (C2) super.get(c2u.get(registry)).get(entId);
        C3 c3 = (C3) super.get(c3u.get(registry)).get(entId);

        t.get(c1, c2, c3);
    }

    public <C1 extends Component<C1>,
            C2 extends Component<C2>,
            C3 extends Component<C3>,
            C4 extends Component<C4>>
    void fetchComps(ComponentRegistry registry, EEntity entity, CompUid<C1> c1u, CompUid<C2> c2u, CompUid<C2> c3u, CompUid<C2> c4u, T4<C1, C2, C3, C4> t) {
        int entId = entity.index();

        C1 c1 = (C1) super.get(c1u.get(registry)).get(entId);
        C2 c2 = (C2) super.get(c2u.get(registry)).get(entId);
        C3 c3 = (C3) super.get(c3u.get(registry)).get(entId);
        C4 c4 = (C4) super.get(c4u.get(registry)).get(entId);

        t.get(c1, c2, c3, c4);
    }

    @FunctionalInterface
    public interface T2<
            C1 extends Component<C1>,
            C2 extends Component<C2>> {
        void get(C1 c1, C2 c2);
    }

    @FunctionalInterface
    public interface T3<
            C1 extends Component<C1>,
            C2 extends Component<C2>,
            C3 extends Component<C3>> {
        void get(C1 c1, C2 c2, C3 c3);
    }

    @FunctionalInterface
    public interface T4<
            C1 extends Component<C1>,
            C2 extends Component<C2>,
            C3 extends Component<C3>,
            C4 extends Component<C4>> {
        void get(C1 c1, C2 c2, C3 c3, C4 c4);
    }
}
