package dev.amble.ait.core.tardis.manager.autojson;

import dev.amble.ait.data.Loyalty;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.base.PrimitiveSchema;

public class LoyaltySchema implements PrimitiveSchema<Loyalty> {

    @Override
    public <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Primitive ctx, Loyalty loyalty) {
        ctx.primitive$value(loyalty.level());
    }

    @Override
    public <To> Loyalty deserialize(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx) {
        return Loyalty.fromLevel(ctx.decode(int.class));
    }
}
