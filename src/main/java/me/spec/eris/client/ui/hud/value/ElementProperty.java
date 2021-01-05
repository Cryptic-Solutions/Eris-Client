package me.spec.eris.client.ui.hud.value;

import java.util.function.Supplier;

public class ElementProperty<T> {

    private String propertyName;
    private T propertyValue;
    private T defaultValue;
    private Supplier<?> supplier;
    private Object parent;

    public ElementProperty(String propertyName, T propertyValue, T defaultValue, Supplier<?> supplier, Object parent) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.defaultValue = defaultValue;
        this.supplier = supplier;
        this.parent = parent;
    }

    public boolean checkDependants() {
        return supplier == null ? true : (Boolean) supplier.get();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public T getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(T propertyValue) {
        this.propertyValue = propertyValue;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Supplier<?> getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier<?> supplier) {
        this.supplier = supplier;
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }
}
