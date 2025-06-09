package dev.amble.ait.core.advancement;

import com.google.gson.JsonObject;
import dev.amble.ait.AITMod;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;


public class SimpleCriterion extends AbstractCriterion<SimpleCriterion.Conditions> {
    protected final Identifier id;

    protected SimpleCriterion(Identifier id) {
        this.id = id;
    }

    @Override
    protected SimpleCriterion.Conditions conditionsFromJson(JsonObject obj,
                                                            LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditions();
    }


    @Override
    public Identifier getId() {
        return this.id;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, SimpleCriterion.Conditions::requirementsMet);
    }

    /**
     * @return a newly created conditions object
     */
    public Conditions conditions() {
        return new Conditions(this.id);
    }

    public SimpleCriterion register() {
        AITMod.LOGGER.info("Registering criterion: {}", this.id);

        Criteria.register(this);
        return this;
    }

    public static SimpleCriterion create(Identifier id) {
        return new SimpleCriterion(id);
    }
    @ApiStatus.Internal
    public static SimpleCriterion create(String name) {
        return new SimpleCriterion(AITMod.id(name));
    }

    public static class Conditions extends AbstractCriterionConditions {
        public Conditions(Identifier id) {
            super(id, LootContextPredicate.EMPTY);
        }

        boolean requirementsMet() {
            return true;
        }
    }
}
