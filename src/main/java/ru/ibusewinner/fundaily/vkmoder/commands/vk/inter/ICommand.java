package ru.ibusewinner.fundaily.vkmoder.commands.vk.inter;

import com.vk.api.sdk.objects.messages.Message;
import ru.ibusewinner.fundaily.vkmoder.data.items.UserStatuses;
import ru.ibusewinner.fundaily.vkmoder.data.items.VkUserItem;

import java.util.List;

public interface ICommand {

    void executeCommand(VkUserItem executor, Message message, List<String> args);

    String getName();

    List<String> getAliases();

    UserStatuses getMinimalStatus();

}
