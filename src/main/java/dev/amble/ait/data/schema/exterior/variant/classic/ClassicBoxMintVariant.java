package dev.amble.ait.data.schema.exterior.variant.classic;

import java.util.Optional;

import dev.amble.ait.data.Loyalty;

public class ClassicBoxMintVariant extends ClassicBoxVariant {
    public ClassicBoxMintVariant() {
        super("mint");
    }

    @Override
    public Optional<Loyalty> requirement() {
        return Optional.of(new Loyalty(Loyalty.Type.COMPANION));
    }
}
