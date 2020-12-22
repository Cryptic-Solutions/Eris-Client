package me.spec.eris.module.values;

import java.util.function.Supplier;

import me.spec.eris.module.Module;

public class Value<T> {
    private String description = "";
    private String valueName;
    private Supplier<?> supplier;
    private T valueObject;
    private T defaultValueObject;
    private Module parent;

    public Value(String valueName, T defaultValueObject, Module parent, Supplier<?> supplier, String description) {
        this.supplier = supplier;
        this.valueName = valueName;
        this.description = description;
        this.defaultValueObject = defaultValueObject;
        this.parent = parent;
        if (valueObject == null) {
            valueObject = defaultValueObject;
        }
        parent.addSetting(this);
    }

    public Value(String valueName, T defaultValueObject, Module parent, Supplier<?> supplier) {
        this.supplier = supplier;
        this.valueName = valueName;
        this.defaultValueObject = defaultValueObject;
        this.parent = parent;
        if (valueObject == null) {
            valueObject = defaultValueObject;
        }
        parent.addSetting(this);
    }

    public Value(String valueName, T defaultValueObject, Module parent) {
        this.supplier = null;
        this.valueName = valueName;
        this.defaultValueObject = defaultValueObject;
        this.parent = parent;
        if (valueObject == null) {
            valueObject = defaultValueObject;
        }
        parent.addSetting(this);
    }

    public String getDescription() {
        return description;
    }

    public boolean checkDependants() {
        return supplier == null ? true : (Boolean) supplier.get();
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public T getValue() {
        return valueObject;
    }

    public void setValueObject(T valueObject) {
        this.valueObject = valueObject;
    }

    public T getDefaultValueObject() {
        return defaultValueObject;
    }

    public void setDefaultValueObject(T defaultValueObject) {
        this.defaultValueObject = defaultValueObject;
    }

    public Module getParent() {
        return parent;
    }

    public void setParent(Module parent) {
        this.parent = parent;
    }
}
