package dev.amble.ait.core.tardis.manager.autojson;

import dev.drtheo.autojson.logger.Logger;
import org.slf4j.LoggerFactory;

public class AITLogger implements Logger {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger("autojson");

    @Override
    public void log(String sep, Object... msgs) {
        String[] strs = new String[msgs.length];
        for (int i = 0; i < msgs.length; i++)
            strs[i] = msgs[i].toString();

        logger.info(String.join(sep, strs));
    }

    @Override
    public void log(Object message) {
        logger.info(message.toString());
    }

    @Override
    public void warn(String sep, Object... msgs) {
        String[] strs = new String[msgs.length];
        for (int i = 0; i < msgs.length; i++)
            strs[i] = msgs[i].toString();

        logger.warn(String.join(sep, strs));
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }
}
