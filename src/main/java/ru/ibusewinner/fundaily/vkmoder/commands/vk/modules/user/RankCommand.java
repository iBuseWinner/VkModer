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

public class RankCommand implements ICommand {

    @Override
    public void executeCommand(VkUserItem executor, Message message, List<String> args) {
        if (message.getText().split(" ").length == 1) {
            long exp = 2 * (executor.getLevel() + 1) * (executor.getLevel() + 1) + 20 * (executor.getLevel() + 1) + 150;

            String answer = "Карточка с опытом "+executor.getNickname()+":" +
                    "\n" +
                    "\nУровень: "+executor.getLevel() +
                    "\nОпыт: "+executor.getXp() +
                    "\nНужно опыта до следующего уровня: "+(exp-executor.getXp());

            try {
                VKModer.getVk().messages().send(VKModer.getActor())
                        .randomId(new Random().nextInt())
                        .forward(
                                new Forward()
                                        .setIsReply(true)
                                        .setConversationMessageIds(List.of(message.getConversationMessageId()))
                                        .setPeerId(message.getPeerId())
                        )
                        .message(answer)
                        .peerId(message.getPeerId())
                        .execute();
            } catch (ClientException | ApiException e) {
                BuseAPI.getBuseLogger().error(e);
            }
        } else {
            //ToDo: Посмотреть другому пользователю.
        }
    }

    @Override
    public String getName() {
        return "rank";
    }

    @Override
    public List<String> getAliases() {
        return List.of("ранг","уровень","опыт","xp","exp");
    }

    @Override
    public UserStatuses getMinimalStatus() {
        return UserStatuses.USER;
    }
}
