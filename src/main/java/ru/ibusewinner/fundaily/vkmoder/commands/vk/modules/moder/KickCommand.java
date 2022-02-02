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

public class KickCommand implements ICommand {
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

            if (executor.getStatus().equals(UserStatuses.MODERATOR) && targetUser.getStatus().equals(UserStatuses.ADMIN)) {
                return;
            }

            String answer = "Эй, @id" + targetUser.getId() + " (" + targetUser.getNickname() + ")! Это была последняя капля!" +
                    "\n" +
                    "\nК сожалению, Вы не поняли наши предупреждения, поэтому мы с Вами прощаемся." +
                    "\n#user" + targetUser.getId() + " #mod" + executor.getId() + " #date" + message.getDate();

            VKModer.getVk().messages().send(VKModer.getActor())
                    .peerId(message.getPeerId())
                    .message(answer)
                    .forward(
                            new Forward()
                                    .setIsReply(true)
                                    .setConversationMessageIds(List.of(message.getReplyMessage().getConversationMessageId()))
                                    .setPeerId(message.getPeerId())
                    )
                    .randomId(new Random().nextInt())
                    .execute();

            VKModer.getVk().messages().removeChatUser(VKModer.getActor(), message.getPeerId()-2000000000).userId(targetUser.getId())
                    .execute();
        } catch (ClientException | ApiException e) {
            BuseAPI.getBuseLogger().error(e);
        }
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public List<String> getAliases() {
        return List.of("кик","исключить","кикнуть");
    }

    @Override
    public UserStatuses getMinimalStatus() {
        return UserStatuses.MODERATOR;
    }
}
