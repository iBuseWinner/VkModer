package ru.ibusewinner.fundaily.vkmoder.utils;

import org.bukkit.scheduler.BukkitRunnable;
import ru.ibusewinner.fundaily.vkmoder.VKModer;
import ru.ibusewinner.fundaily.vkmoder.data.items.VkUserItem;

public class UpdateUserInfo extends BukkitRunnable {
    @Override
    public void run() {
        for (VkUserItem user : VKModer.getCachedUsers()) {
            VKModer.getMySQL().updateUser(user);
        }
    }
}
