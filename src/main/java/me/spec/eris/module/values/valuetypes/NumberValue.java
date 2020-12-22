package me.spec.eris.module.values.valuetypes;

import java.util.function.Supplier;

import me.spec.eris.module.Module;
import me.spec.eris.module.values.Value;

public class NumberValue<T> extends Value<T> {

    private T minimumValue, maximumValue;

    public NumberValue(String valueName, T defaultValueObject, T minimumValue, T maximumValue, Module parent, Supplier<?> supplier, String description) {
        super(valueName, defaultValueObject, parent, supplier, description);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public NumberValue(String valueName, T defaultValueObject, T minimumValue, T maximumValue, Module parent, String description) {
        super(valueName, defaultValueObject, parent, null, description);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public NumberValue(String valueName, T defaultValueObject, T minimumValue, T maximumValue, Module parent) {
        super(valueName, defaultValueObject, parent, null, "");
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public T getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(T minimumValue) {
        this.minimumValue = minimumValue;
    }

    public T getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(T maximumValue) {
        this.maximumValue = maximumValue;
    }

}
