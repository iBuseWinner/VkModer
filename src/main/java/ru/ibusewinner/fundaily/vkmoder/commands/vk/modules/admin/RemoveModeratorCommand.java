package ru.ibusewinner.fundaily.vkmoder.commands.vk.modules.admin;

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

public class RemoveModeratorCommand implements ICommand {
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

            targetUser.setStatus(UserStatuses.USER);

            VKModer.getVk().messages().send(VKModer.getActor())
                    .randomId(new Random().nextInt())
                    .forward(
                            new Forward()
                                    .setIsReply(true)
                                    .setConversationMessageIds(List.of(message.getReplyMessage().getConversationMessageId()))
                                    .setPeerId(message.getPeerId())
                    )
                    .peerId(message.getPeerId())
                    .message("Ура, @id"+targetUser.getId()+" ("+targetUser.getNickname()+"), теперь ты Модератор бесед!")
                    .execute();
        } catch (ClientException | ApiException e) {
            BuseAPI.getBuseLogger().error(e);
        }
    }

    @Override
    public String getName() {
        return "removemoderator";
    }

    @Override
    public List<String> getAliases() {
        return List.of("removemoder","-moder","снятьмодера","снятьмодератора","-модер","-модератор","-мод","-mod");
    }

    @Override
    public UserStatuses getMinimalStatus() {
        return UserStatuses.ADMIN;
    }
}
