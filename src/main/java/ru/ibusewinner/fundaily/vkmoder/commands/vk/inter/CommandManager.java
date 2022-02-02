package ru.ibusewinner.fundaily.vkmoder.commands.vk.inter;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Forward;
import com.vk.api.sdk.objects.messages.Message;
import ru.ibusewinner.fundaily.vkmoder.VKModer;
import ru.ibusewinner.fundaily.vkmoder.commands.vk.modules.admin.AddModeratorCommand;
import ru.ibusewinner.fundaily.vkmoder.commands.vk.modules.admin.RemoveModeratorCommand;
import ru.ibusewinner.fundaily.vkmoder.commands.vk.modules.moder.ChangeNickCommand;
import ru.ibusewinner.fundaily.vkmoder.commands.vk.modules.moder.KickCommand;
import ru.ibusewinner.fundaily.vkmoder.commands.vk.modules.moder.UnwarnCommand;
import ru.ibusewinner.fundaily.vkmoder.commands.vk.modules.moder.WarnCommand;
import ru.ibusewinner.fundaily.vkmoder.commands.vk.modules.user.NicknameCommand;
import ru.ibusewinner.fundaily.vkmoder.commands.vk.modules.user.RankCommand;
import ru.ibusewinner.fundaily.vkmoder.data.items.UserStatuses;
import ru.ibusewinner.fundaily.vkmoder.data.items.VkUserItem;
import ru.ibusewinner.fundaily.vkmoder.utils.XPManager;
import ru.ibusewinner.plugin.buseapi.BuseAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager() {
        //Admin commands
        addCommand(new AddModeratorCommand());
        addCommand(new RemoveModeratorCommand());

        //Moder commands
        addCommand(new ChangeNickCommand());
        addCommand(new KickCommand());
        addCommand(new WarnCommand());
        addCommand(new UnwarnCommand());

        //User commands
        addCommand(new NicknameCommand());
        addCommand(new RankCommand());
    }

    private void addCommand(ICommand command) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(command.getName()));

        if (nameFound) {
            BuseAPI.getBuseLogger().info("&cКоманда "+command.getName()+" уже существует!");
        } else {
            commands.add(command);
        }
    }

    private ICommand getCommand(String search) {
        String lower = search.toLowerCase();

        for (ICommand command : this.commands) {
            if (command.getName().equals(lower) || command.getAliases().contains(lower)) {
                return command;
            }
        }
        return null;
    }

    public void handle(VkUserItem userItem, Message message) {
        String[] split = message.getText()
                .replaceFirst("(?i)"+ Pattern.quote(VKModer.getCommandsPrefix()), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        ICommand command = this.getCommand(invoke);

        if (command != null) {
            List<String> args = Arrays.asList(split).subList(1, split.length);
            BuseAPI.getBuseLogger().info("Пользователь "+userItem.getId()+" использовал команду "+command.getName());
            boolean canBeExecuted = false;
            if (command.getMinimalStatus().equals(UserStatuses.USER)) {
                canBeExecuted = true;
            } else if (command.getMinimalStatus().equals(UserStatuses.MODERATOR) &&
                    (userItem.getStatus().equals(UserStatuses.MODERATOR) || userItem.getStatus().equals(UserStatuses.ADMIN))) {
                canBeExecuted = true;
            } else if (command.getMinimalStatus().equals(UserStatuses.ADMIN) && userItem.getStatus().equals(UserStatuses.ADMIN)) {
                canBeExecuted = true;
            }
            if (canBeExecuted) {
                command.executeCommand(userItem, message, args);
            }
        } else {
            if (VKModer.getSettings().getConfig().getBoolean("vk.xp-enabled") &&
                    XPManager.calculateXP(userItem, message.getText())) {
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
