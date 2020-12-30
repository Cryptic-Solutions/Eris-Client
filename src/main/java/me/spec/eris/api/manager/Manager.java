package me.spec.eris.api.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Manager<T> {

    protected final List<T> managerArraylist = new ArrayList<>();

    public Manager() {
        loadManager();
    }

    public void loadManager() {}

    public List<T> getManagerArraylist() {
        return managerArraylist;
    }

    public void addToManagerArraylist(T object) {
        managerArraylist.add(object);
    }

    public void addToManagerArraylist(T...elements) {
        managerArraylist.addAll(Arrays.asList(elements));
    }

    public void removeFromManagerArraylist(T object) {
        managerArraylist.remove(object);
    }

    public void removeFromManagerArraylistIndex(int index) {
        managerArraylist.remove(index);
    }
}
