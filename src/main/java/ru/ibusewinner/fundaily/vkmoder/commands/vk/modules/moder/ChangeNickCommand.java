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

public class ChangeNickCommand implements ICommand {
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

            if (message.getText().split(" ").length == 1) {
                VKModer.getVk().messages().send(VKModer.getActor())
                        .randomId(new Random().nextInt())
                        .forward(
                                new Forward()
                                        .setIsReply(true)
                                        .setConversationMessageIds(List.of(message.getReplyMessage().getConversationMessageId()))
                                        .setPeerId(message.getPeerId())
                        )
                        .peerId(message.getPeerId())
                        .message("Текущий ник пользователя: "+targetUser.getNickname())
                        .execute();
            } else {
                String nickname = message.getText().substring(message.getText().split(" ")[0].length()+1);
                if (nickname.length() > 30) {
                    nickname = nickname.substring(0, 15);
                }
                targetUser.setNickname(nickname);
                VKModer.getVk().messages().send(VKModer.getActor())
                        .randomId(new Random().nextInt())
                        .forward(
                                new Forward()
                                        .setIsReply(true)
                                        .setConversationMessageIds(List.of(message.getReplyMessage().getConversationMessageId()))
                                        .setPeerId(message.getPeerId())
                        )
                        .peerId(message.getPeerId())
                        .message("Новый ник пользователя: "+targetUser.getNickname())
                        .execute();
            }

        } catch (ClientException | ApiException e) {
            BuseAPI.getBuseLogger().error(e);
        }
    }

    @Override
    public String getName() {
        return "changenickname";
    }

    @Override
    public List<String> getAliases() {
        return List.of("changenick","изменитьник","изменитьникнейм","сменитьник","сменитьникнейм");
    }

    @Override
    public UserStatuses getMinimalStatus() {
        return UserStatuses.MODERATOR;
    }
}
