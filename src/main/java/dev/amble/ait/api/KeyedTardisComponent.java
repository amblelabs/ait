package dev.amble.ait.api;

import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.properties.PropertyMap;
import dev.amble.ait.data.properties.Value;

public abstract class KeyedTardisComponent extends TardisComponent {

    @Exclude(strategy = Exclude.Strategy.FILE)
    private PropertyMap data = new PropertyMap();

    /**
     * Do NOT under any circumstances run logic in this constructor. Default field
     * values should be inlined. All logic should be done in an appropriate init
     * method.
     *
     * @implNote The {@link TardisComponent#tardis()} will always be null at the
     *           time this constructor gets called.
     */
    public KeyedTardisComponent(IdLike id) {
        super(id);
    }

    @Override
    protected void init(InitContext context) {
        if (this.data == null)
            this.data = new PropertyMap();

        super.init(context);
    }

    public void register(Value<?> property) {
        this.data.put(property.getProperty().getName(), property);
    }

    @Override
    public void dispose() {
        super.dispose();

        this.data.dispose();
    }

    public PropertyMap getPropertyData() {
        return data;
    }
}
