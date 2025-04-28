package dev.amble.ait.core.tardis.manager.autojson;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.engine.SubSystem;
import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.logger.Logger;
import org.slf4j.LoggerFactory;

public class CustomAutoJSON extends AutoJSON {

    @Override
    protected Logger setupLogger() {
        return new AITLogger();
    }

    @Override
    public boolean safeInstancing(Class<?> type) {
        if (TardisComponent.class.isAssignableFrom(type))
            return true;

        if (SubSystem.class.isAssignableFrom(type))
            return true;

        return super.safeInstancing(type);
    }
}
