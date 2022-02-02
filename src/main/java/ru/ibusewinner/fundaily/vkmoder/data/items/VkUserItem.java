package ru.ibusewinner.fundaily.vkmoder.data.items;

public class VkUserItem {
    private int id;
    private String nickname;
    private UserStatuses status;
    private long level;
    private long xp;
    private int warnings;

    public VkUserItem(int id, String nickname, UserStatuses status, long level, long xp, int warnings) {
        this.id = id;
        this.nickname = nickname;
        this.status = status;
        this.level = level;
        this.xp = xp;
        this.warnings = warnings;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public int getWarnings() {
        return warnings;
    }

    public long getLevel() {
        return level;
    }

    public long getXp() {
        return xp;
    }

    public UserStatuses getStatus() {
        return status;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setStatus(UserStatuses status) {
        this.status = status;
    }

    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public void setLevel(long level) {
        this.level = level;
    }
}
