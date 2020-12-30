package me.spec.eris.api.manager;

import java.util.ArrayList;
import java.util.List;

public abstract class Manager<T> {

    protected final List<T> managerArraylist = new ArrayList<>();

    public Manager() {
        loadManager();
    }

    public abstract void loadManager();

    public List<T> getManagerArraylist() {
        return managerArraylist;
    }

    public void addToManagerArraylist(T object) {
        managerArraylist.add(object);
    }

    public void removeFromManagerArraylist(T object) {
        managerArraylist.remove(object);
    }

    public void removeFromManagerArraylistIndex(int index) {
        managerArraylist.remove(index);
    }
}
