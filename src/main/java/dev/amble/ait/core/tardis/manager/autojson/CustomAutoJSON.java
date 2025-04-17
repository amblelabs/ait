package dev.amble.ait.core.tardis.manager.autojson;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.engine.SubSystem;
import dev.drtheo.autojson.AutoJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomAutoJSON extends AutoJSON {

    public static final Logger LOGGER = LoggerFactory.getLogger("autojson");

    @Override
    public boolean safeInstancing(Class<?> type) {
        if (TardisComponent.class.isAssignableFrom(type))
            return true;

        if (SubSystem.class.isAssignableFrom(type))
            return true;

        return super.safeInstancing(type);
    }

    @Override
    public void log(String message) {
        LOGGER.info(message);
    }

    @Override
    public void warn(String message) {
        LOGGER.warn(message);
    }
}
