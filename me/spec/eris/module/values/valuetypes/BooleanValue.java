package me.spec.eris.module.values.valuetypes;

import java.util.function.Supplier;

import me.spec.eris.module.Module;
import me.spec.eris.module.values.Value;

/**
 * Author: Ice
 * Created: 18:41, 12-Jun-20
 * Project: Client
 */
public class BooleanValue<T> extends Value<T> {
    public BooleanValue(String valueName, T defaultValueObject, Module parent, Supplier supplier, String description) {
        super(valueName, defaultValueObject, parent, supplier, description);
    }

    public BooleanValue(String valueName, T defaultValueObject, Module parent, String description) {
        super(valueName, defaultValueObject, parent, null, description);
    }
    public BooleanValue(String valueName, T defaultValueObject, Module parent) {
        super(valueName, defaultValueObject, parent, null, "");
    }
}
