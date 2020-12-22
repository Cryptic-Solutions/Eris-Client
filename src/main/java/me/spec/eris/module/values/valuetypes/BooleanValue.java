package me.spec.eris.module.values.valuetypes;

import java.util.function.Supplier;

import me.spec.eris.module.Module;
import me.spec.eris.module.values.Value;

public class BooleanValue<T> extends Value<T> {
    public BooleanValue(String valueName, T defaultValueObject, Module parent, Supplier<?> supplier, String description) {
        super(valueName, defaultValueObject, parent, supplier, description);
    }

    public BooleanValue(String valueName, T defaultValueObject, Module parent, String description) {
        super(valueName, defaultValueObject, parent, null, description);
    }

    public BooleanValue(String valueName, T defaultValueObject, Module parent) {
        super(valueName, defaultValueObject, parent, null, "");
    }
}
