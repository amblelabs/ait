package dev.amble.ait.core.tardis.handler;

import java.util.HashSet;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;

public class DatabaseHandler extends KeyedTardisComponent {
    private static final Property<HashSet<String>> PEOPLE = new Property<>(Property.STR_SET, "people",
            new HashSet<>());
    private final Value<HashSet<String>> people = PEOPLE.create(this);
    public DatabaseHandler() {
        super(Id.DATABASE);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        people.of(this, PEOPLE);
    }

    public void addPerson(String person) {
        addPerson(person, true);
    }

    public void addPerson(String person, boolean sync) {
        if (person == null || person.isEmpty()) return;
        people.flatMap(strings -> {
            strings.add(person);
            return strings;
        }, sync);
    }
}
