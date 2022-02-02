package ru.ibusewinner.fundaily.vkmoder.vk;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Conversation;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import ru.ibusewinner.fundaily.vkmoder.VKModer;
import ru.ibusewinner.plugin.buseapi.BuseAPI;

import java.util.List;

public class VkManager {

    public static Message getMessage() {
        try {
            MessagesGetLongPollHistoryQuery eventsQuery = VKModer.getVk().messages()
                    .getLongPollHistory(VKModer.getActor())
                    .ts(VKModer.getTs());

            if (VKModer.getMaxMsgId() > 0) {
                eventsQuery.maxMsgId(VKModer.getMaxMsgId());
            }
            List<Message> messages = eventsQuery.execute().getMessages().getItems();

            if (!messages.isEmpty()) {
                VKModer.setTs(VKModer.getVk().messages().getLongPollServer(VKModer.getActor()).execute().getTs());
            }

            if (!messages.isEmpty() && !messages.get(0).isOut()) {
                if (messages.get(0).getId() > VKModer.getMaxMsgId()) {
                    VKModer.setMaxMsgId(messages.get(0).getId());
                }
                return messages.get(0);
            }
        } catch (ClientException | ApiException e) {
            BuseAPI.getBuseLogger().error(e);
        }
        return null;
    }

    public static String getFirstAndLastNames(int userId) {
        try {
            List<GetResponse> getResponses = VKModer.getVk().users().get(VKModer.getActor()).userIds(String.valueOf(userId)).execute();
            return getResponses.get(0).getFirstName()+" "+getResponses.get(0).getLastName();
        } catch (ClientException | ApiException e) {
            BuseAPI.getBuseLogger().error(e);
        }
        return "^-^";
    }

    public static String getChatTitle(int peerId) {
        try {
            List<Conversation> conversations = VKModer.getVk().messages().getConversationsById(VKModer.getActor(), peerId).extended(false).execute().getItems();
            return conversations.get(0).getChatSettings().getTitle();
        } catch (ClientException | ApiException e) {
            BuseAPI.getBuseLogger().error(e);
        }
        return "^-^";
    }

}
