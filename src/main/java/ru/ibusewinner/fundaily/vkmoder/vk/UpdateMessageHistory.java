package ru.ibusewinner.fundaily.vkmoder.vk;

import com.vk.api.sdk.objects.messages.Message;
import org.bukkit.scheduler.BukkitRunnable;
import ru.ibusewinner.fundaily.vkmoder.VKModer;
import ru.ibusewinner.fundaily.vkmoder.data.items.VkUserItem;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateMessageHistory extends BukkitRunnable {
    @Override
    public void run() {
        Message message = VkManager.getMessage();
        if (message != null) {
            ExecutorService executorService = Executors.newCachedThreadPool();
            executorService.execute(new Messenger(message));
        }

        Iterator<VkUserItem> iterator = VKModer.getAntiCommandFlood().keySet().iterator();
        while (iterator.hasNext()) {
            VkUserItem element = iterator.next();
            if (VKModer.getAntiCommandFlood().get(element) <= System.currentTimeMillis()) {
                iterator.remove();
            }
        }

        Iterator<VkUserItem> iterator2 = VKModer.getAntiFlood().keySet().iterator();
        while (iterator2.hasNext()) {
            VkUserItem element = iterator2.next();
            if (VKModer.getAntiFlood().get(element) <= System.currentTimeMillis()) {
                iterator2.remove();
            }
        }
    }
}
