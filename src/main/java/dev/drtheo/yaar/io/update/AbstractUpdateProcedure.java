package dev.drtheo.yaar.io.update;

import com.google.gson.JsonObject;

public interface AbstractUpdateProcedure {
    JsonObject update(JsonObject obj);
}
