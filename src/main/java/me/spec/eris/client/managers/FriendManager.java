package me.spec.eris.client.managers;

import me.spec.eris.api.friend.Friend;
import me.spec.eris.api.manager.Manager;

public class FriendManager extends Manager<Friend> {


    @Override
    public void loadManager() {

    }

    public Friend getFriendByName(String friendName) {
        for(Friend friend : getManagerArraylist()) {
            if(friend.getFriendName().equalsIgnoreCase(friendName)) {
                return friend;
            }
        }
        return null;
    }
}
