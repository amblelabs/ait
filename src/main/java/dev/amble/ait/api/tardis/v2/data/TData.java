package dev.amble.ait.api.tardis.v2.data;

import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.api.tardis.v2.data.properties.PropertyMap;
import dev.amble.ait.api.tardis.v2.data.properties.Value;

public interface TData<Self extends TData<Self>> extends Disposable {

    default void onAttach() { }

    TDataHolder<Self> holder();

    default int index() {
        return holder().index();
    }

    abstract class Basic<Self extends Basic<Self>> implements TData<Self> {

        protected boolean dirty = false;

        public void markDirty() {
            this.dirty = true;
        }

        public void unmarkDirty() {
            this.dirty = false;
        }

        public boolean dirty() {
            return dirty;
        }
    }

    abstract class Props<Self extends Props<Self>> extends Basic<Self> {

        private final PropertyMap properties = new PropertyMap();

        public void register(Value<?> property) {
            this.properties.put(property.getProperty().getName(), property);
        }

        public PropertyMap getPropertyData() {
            return properties;
        }

        @Override
        public void dispose() {
            this.properties.dispose();
        }
    }
}
