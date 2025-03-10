package dev.amble.ait.api;

public abstract class Initializable<T extends Initializable.Context> {

    protected static <T extends Initializable.Context> void init(Initializable<T> component, T context) {
        component.init(context);
    }

    protected void init(T context) {
        this.onEarlyInit(context);

        if (context.deserialized()) {
            this.onLoaded();
        } else {
            this.onCreate();
        }

        this.onInit(context);
    }

    /**
     * Called first in the initialization sequence.
     */
    protected void onEarlyInit(T ctx) {
    }

    /**
     * Called after a {@link #onLoaded()} or {@link #onCreate()} is called.
     */
    protected void onInit(T ctx) {
    }

    /**
     * Called when the component is created. Server-side only.
     */
    public void onCreate() {
    }

    /**
     * Called when the component is loaded from a file or received on the client.
     */
    public void onLoaded() {
    }

    public interface Context {
        boolean created();

        boolean deserialized();
    }
}
