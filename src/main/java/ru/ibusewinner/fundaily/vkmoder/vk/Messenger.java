package ru.ibusewinner.fundaily.vkmoder.vk;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Forward;
import com.vk.api.sdk.objects.messages.Message;
import ru.ibusewinner.fundaily.vkmoder.VKModer;
import ru.ibusewinner.fundaily.vkmoder.data.items.UserStatuses;
import ru.ibusewinner.fundaily.vkmoder.data.items.VkUserItem;
import ru.ibusewinner.fundaily.vkmoder.utils.TextNormalizer;
import ru.ibusewinner.fundaily.vkmoder.utils.XPManager;
import ru.ibusewinner.plugin.buseapi.BuseAPI;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Messenger implements Runnable {

    private Message message;

    public Messenger(Message message) {
        this.message = message;
    }

    @Override
    public void run() {
        if (message.getPeerId() > 2000000000) {
            VkUserItem userItem = VKModer.getUserById(message.getFromId());
            if (userItem == null) {
                userItem = VKModer.getMySQL().getUserFromData(message.getFromId());
                if (userItem == null) {
                    userItem = new VkUserItem(
                            message.getFromId(), VkManager.getFirstAndLastNames(message.getFromId()), UserStatuses.USER, 0, 0, 0
                    );
                    VKModer.getMySQL().createUserAndCacheIt(userItem);
                }
            }

            String text = message.getText();
            //Анти-мат фильтр
            if (VKModer.getSettings().getConfig().getIntegerList("vk.mod-chats").contains(message.getPeerId() - 2000000000)) {
                String normalText = TextNormalizer.replaceAll(text);

                if (!userItem.getStatus().equals(UserStatuses.ADMIN) &&
                        Arrays.stream(normalText.split(" ")).anyMatch(s -> VKModer.getBanwords().contains(s))) {
                    userItem.setWarnings(userItem.getWarnings()+1);

                    if (userItem.getWarnings() < VKModer.getMaxWarnings()) {
                        String answer = "Эй, @id"+message.getFromId()+" ("+userItem.getNickname()+")! Не нарушайте правила беседы!" +
                                "\n" +
                                "\nВы получаете "+userItem.getWarnings()+" из "+VKModer.getMaxWarnings()+" предупреждение! " +
                                "В следующий раз будьте аккуратнее со словами." +
                                "\n#user"+userItem.getId()+" #autowarn #date"+message.getDate();

                        try {
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
                    } else {
                        String answer = "Эй, @id"+message.getFromId()+" ("+userItem.getNickname()+")! Это была последняя капля!" +
                                "\n" +
                                "\nК сожалению, Вы не поняли наши предупреждения, поэтому мы с Вами прощаемся." +
                                "\n#user"+userItem.getId()+" #autowarn #date"+message.getDate();

                        try {
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

                            VKModer.getVk().messages().removeChatUser(
                                    VKModer.getActor(), message.getPeerId()-2000000000).userId(message.getFromId())
                                    .execute();
                        } catch (ClientException | ApiException e) {
                            BuseAPI.getBuseLogger().error(e);
                        }
                    }
                }
            } else {
                BuseAPI.getBuseLogger().info("Чат " + VkManager.getChatTitle(message.getPeerId()) + " (" + message.getPeerId() + ") " +
                        "не находится в списке разрешённых!");
            }

            if (text.startsWith(VKModer.getCommandsPrefix())) {
                VKModer.getCommandManager().handle(userItem, message);
            } else {
                if (VKModer.getSettings().getConfig().getBoolean("vk.xp-enabled") &&
                        XPManager.calculateXP(userItem, text)) {
                    try {
                        VKModer.getVk().messages().send(VKModer.getActor())
                                .randomId(new Random().nextInt())
                                .forward(
                                        new Forward()
                                                .setIsReply(true)
                                                .setConversationMessageIds(List.of(message.getConversationMessageId()))
                                                .setPeerId(message.getPeerId())
                                )
                                .peerId(message.getPeerId())
                                .message("Ура, @id"+userItem.getId()+" ("+userItem.getNickname()+") достиг нового уровня -> "+userItem.getLevel())
                                .execute();
                    } catch (ClientException | ApiException e) {
                        BuseAPI.getBuseLogger().error(e);
                    }
                }
            }
        }
    }
}
