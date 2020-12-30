package me.spec.eris.client.managers;

import me.spec.eris.Eris;
import me.spec.eris.api.friend.Friend;
import me.spec.eris.api.manager.Manager;
import net.minecraft.entity.EntityLivingBase;

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

    public boolean isFriend(EntityLivingBase elb) {
        for(Friend friend : Eris.getInstance().friendManager.getManagerArraylist()) {
            if(friend.getFriendName().equalsIgnoreCase(elb.getName())) {
                return true;
            }
        }
        return false;
    }
}
