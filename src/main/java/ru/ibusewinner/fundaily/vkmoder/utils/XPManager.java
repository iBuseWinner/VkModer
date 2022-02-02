package ru.ibusewinner.fundaily.vkmoder.utils;

import ru.ibusewinner.fundaily.vkmoder.VKModer;
import ru.ibusewinner.fundaily.vkmoder.data.items.VkUserItem;

import java.util.Random;

public class XPManager {

    public static boolean calculateXP(VkUserItem user, String message) {
        if (!VKModer.getAntiFlood().containsKey(user)) {
            if (message.length() <= 10 || message.length() >= 1000) {
                return false;
            }

            Random randomizer = new Random(System.currentTimeMillis());
            int min = 10;
            int max = 25;
            int random = randomizer.nextInt(max - min) + min;

            user.setXp(user.getXp() + random);
            VKModer.getAntiFlood().put(user, System.currentTimeMillis()+(1000*10));

            //Level formula
            long exp = 2 * (user.getLevel() + 1) * (user.getLevel() + 1) + 20 * (user.getLevel() + 1) + 150;

            if (user.getXp() >= exp) {
                user.setLevel(user.getLevel() + 1);
                user.setXp(user.getXp() - exp);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
