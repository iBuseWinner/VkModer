package ru.ibusewinner.fundaily.vkmoder.commands.vk.modules.moder;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Forward;
import com.vk.api.sdk.objects.messages.Message;
import ru.ibusewinner.fundaily.vkmoder.VKModer;
import ru.ibusewinner.fundaily.vkmoder.commands.vk.inter.ICommand;
import ru.ibusewinner.fundaily.vkmoder.data.items.UserStatuses;
import ru.ibusewinner.fundaily.vkmoder.data.items.VkUserItem;
import ru.ibusewinner.fundaily.vkmoder.vk.VkManager;
import ru.ibusewinner.plugin.buseapi.BuseAPI;

import java.util.List;
import java.util.Random;

public class UnwarnCommand implements ICommand {
    @Override
    public void executeCommand(VkUserItem executor, Message message, List<String> args) {
        try {
            if (message.getReplyMessage() == null) {
                return;
            }
            VkUserItem targetUser = VKModer.getUserById(message.getReplyMessage().getFromId());
            if (targetUser == null) {
                targetUser = VKModer.getMySQL().getUserFromData(message.getFromId());
                if (targetUser == null) {
                    targetUser = new VkUserItem(
                            message.getFromId(), VkManager.getFirstAndLastNames(message.getFromId()), UserStatuses.USER, 0, 0, 0
                    );
                    VKModer.getMySQL().createUserAndCacheIt(targetUser);
                }
            }

            if (targetUser.getWarnings() > 0) {
                targetUser.setWarnings(targetUser.getWarnings()-1);
            }

            String answer = "Крайнее предупреждение @id" + targetUser.getId() + " (" + targetUser.getNickname() + ") было снято, " +
                      "если оно существовало." +
                    "\n" +
                    "\n#user" + targetUser.getId() + " #mod" + executor.getId() + " #date" + message.getDate();

            VKModer.getVk().messages().send(VKModer.getActor())
                    .peerId(message.getPeerId())
                    .message(answer)
                    .forward(
                            new Forward()
                                    .setIsReply(true)
                                    .setConversationMessageIds(List.of(message.getConversationMessageId()))
                                    .setPeerId(message.getPeerId())
                    )
                    .randomId(new Random().nextInt())
                    .execute();
        } catch (ClientException | ApiException e) {
            BuseAPI.getBuseLogger().error(e);
        }
    }

    @Override
    public String getName() {
        return "unwarn";
    }

    @Override
    public List<String> getAliases() {
        return List.of("снятьпред","-пред","снятьпредупреждение","-предупреждение","removewarn","removewarning");
    }

    @Override
    public UserStatuses getMinimalStatus() {
        return UserStatuses.MODERATOR;
    }
}
