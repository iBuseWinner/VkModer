package ru.ibusewinner.fundaily.vkmoder.commands.vk.modules.user;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Forward;
import com.vk.api.sdk.objects.messages.Message;
import ru.ibusewinner.fundaily.vkmoder.VKModer;
import ru.ibusewinner.fundaily.vkmoder.commands.vk.inter.ICommand;
import ru.ibusewinner.fundaily.vkmoder.data.items.UserStatuses;
import ru.ibusewinner.fundaily.vkmoder.data.items.VkUserItem;
import ru.ibusewinner.plugin.buseapi.BuseAPI;

import java.util.List;
import java.util.Random;

public class NicknameCommand implements ICommand {

    @Override
    public void executeCommand(VkUserItem executor, Message message, List<String> args) {
        try {
            if (message.getText().split(" ").length == 1) {
                VKModer.getVk().messages().send(VKModer.getActor())
                        .randomId(new Random().nextInt())
                        .forward(
                                new Forward()
                                        .setIsReply(true)
                                        .setConversationMessageIds(List.of(message.getConversationMessageId()))
                                        .setPeerId(message.getPeerId())
                        )
                        .peerId(message.getPeerId())
                        .message("Твой текущий ник: "+executor.getNickname())
                        .execute();
            } else {
                String nickname = message.getText().substring(message.getText().split(" ")[0].length()+1);
                if (nickname.length() > 30) {
                    nickname = nickname.substring(0, 15);
                }
                executor.setNickname(nickname);
                VKModer.getVk().messages().send(VKModer.getActor())
                        .randomId(new Random().nextInt())
                        .forward(
                                new Forward()
                                        .setIsReply(true)
                                        .setConversationMessageIds(List.of(message.getConversationMessageId()))
                                        .setPeerId(message.getPeerId())
                        )
                        .peerId(message.getPeerId())
                        .message("Твой новый ник: "+executor.getNickname())
                        .execute();
            }
        } catch (ClientException | ApiException e) {
            BuseAPI.getBuseLogger().error(e);
        }
    }

    @Override
    public String getName() {
        return "nickname";
    }

    @Override
    public List<String> getAliases() {
        return List.of("nick","ник","никнейм");
    }

    @Override
    public UserStatuses getMinimalStatus() {
        return UserStatuses.USER;
    }
}
