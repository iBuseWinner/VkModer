package ru.ibusewinner.fundaily.vkmoder.data.items;

public class UserCooldown {

    private VkUserItem userItem;
    private long cooldown;
    private CooldownType cooldownType;

    public UserCooldown(VkUserItem userItem, long cooldown, CooldownType cooldownType) {
        this.userItem = userItem;
        this.cooldown = cooldown;
        this.cooldownType = cooldownType;
    }

    public long getCooldown() {
        return cooldown;
    }

    public CooldownType getCooldownType() {
        return cooldownType;
    }

    public VkUserItem getUserItem() {
        return userItem;
    }
}
