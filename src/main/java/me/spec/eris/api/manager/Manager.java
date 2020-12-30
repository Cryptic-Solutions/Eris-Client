package me.spec.eris.api.manager;

import java.util.ArrayList;

public abstract class Manager<T> {

    private ArrayList<T> managerArraylist = new ArrayList<>();

    public Manager() {
        loadManager();
    }

    public abstract void loadManager();

    public ArrayList<T> getManagerArraylist() {
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
