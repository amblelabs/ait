package dev.amble.ait.core.tardis.handler;

import java.util.HashSet;
import java.util.UUID;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;

public class DatabaseHandler extends KeyedTardisComponent {
    private static final Property<HashSet<UUID>> PEOPLE = new Property<>(Property.UUID_SET, "people",
            new HashSet<>());
    private final Value<HashSet<UUID>> people = PEOPLE.create(this);
    public DatabaseHandler() {
        super(Id.DATABASE);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        people.of(this, PEOPLE);
    }

    public void addPerson(UUID person) {
        addPerson(person, true);
    }

    public void addPerson(UUID person, boolean sync) {
        if (person == null) return;
        people.flatMap(uuids -> {
            uuids.add(person);
            return uuids;
        }, sync);
    }
}
