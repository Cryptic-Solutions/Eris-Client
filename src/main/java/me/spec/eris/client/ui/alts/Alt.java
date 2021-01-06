package me.spec.eris.client.ui.alts;

import java.util.HashMap;

public class Alt {

    private String user;
    private String pass;
    private HashMap<String, Boolean> banMap = new HashMap<>();

    public Alt(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public HashMap<String, Boolean> getBanMap() {
        return banMap;
    }
}
