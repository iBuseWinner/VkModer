package ru.ibusewinner.fundaily.vkmoder.data;

import ru.ibusewinner.fundaily.vkmoder.VKModer;
import ru.ibusewinner.fundaily.vkmoder.data.items.UserStatuses;
import ru.ibusewinner.fundaily.vkmoder.data.items.VkUserItem;
import ru.ibusewinner.plugin.buseapi.BuseAPI;
import ru.ibusewinner.plugin.buseapi.mysql.MySQL;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class VkMySQL extends MySQL {

    public VkMySQL(String host, int port, String database, String args, String user, String password) {
        super(host, port, database, args, user, password);
    }

    public void createTables() {
        getPreparedStatement("CREATE TABLE IF NOT EXISTS `banwords` " +
                "(`word` VARCHAR(50) NOT NULL DEFAULT '-', " +
                "`weight` INT(5) NOT NULL DEFAULT '0' " +
                ") ENGINE=INNODB CHARSET=utf8 COLLATE 'utf8_general_ci';", preparedStatement -> {
            try {
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                BuseAPI.getBuseLogger().error(e);
            }
        });

        getPreparedStatement("CREATE TABLE IF NOT EXISTS `users` " +
                "(`vkId` INT(50) NOT NULL, " +
                "`nickname` VARCHAR(50) NOT NULL DEFAULT '0', " +
                "`status` VARCHAR(50) NOT NULL DEFAULT '0', " +
                "`level` BIGINT(50) NOT NULL DEFAULT '0', " +
                "`xp` BIGINT(50) NOT NULL DEFAULT '0', " +
                "`warnings` INT(5) NOT NULL DEFAULT '0') COLLATE='utf8_general_ci' ENGINE=InnoDB;", preparedStatement -> {
            try {
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                BuseAPI.getBuseLogger().error(e);
            }
        });
    }

    public VkUserItem getUserFromData(int id) {
        AtomicReference<VkUserItem> vkUserItem = new AtomicReference<>();
        getResultSet("SELECT * FROM `users` WHERE `vkId`='"+id+"';", resultSet -> {
            try {
                if (resultSet.next()) {
                    String nickname = resultSet.getString("nickname");
                    UserStatuses status = UserStatuses.valueOf(resultSet.getString("status").toUpperCase());
                    long level = resultSet.getLong("level");
                    long xp = resultSet.getLong("xp");
                    int warnings = resultSet.getInt("warnings");
                    vkUserItem.set(new VkUserItem(id, nickname, status, level, xp, warnings));
                    VKModer.getCachedUsers().add(vkUserItem.get());
                }
                resultSet.close();
            } catch (SQLException e) {
                BuseAPI.getBuseLogger().error(e);
            }
        });
        return vkUserItem.get();
    }

    public void createUserAndCacheIt(VkUserItem user) {
        getPreparedStatement("INSERT INTO `users` (`vkId`,`nickname`) VALUES ('"+user.getId()+"', '"+user.getNickname()+"');", preparedStatement -> {
            try {
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                BuseAPI.getBuseLogger().error(e);
            }
        });

        VKModer.getCachedUsers().add(user);
    }

    public void addBanwords() {
        getResultSet("SELECT * FROM `banwords`;", resultSet -> {
            try {
                while (resultSet.next()) {
                    VKModer.getBanwords().add(resultSet.getString("word"));
                }
                resultSet.close();
            } catch (SQLException e) {
                BuseAPI.getBuseLogger().error(e);
            }
        });
    }

    public void updateUser(VkUserItem user) {
        getPreparedStatement("UPDATE `users` SET " +
                "`nickname`='"+user.getNickname()+"', " +
                "`status`='"+user.getStatus().toString()+"', " +
                "`level`='"+user.getLevel()+"', " +
                "`xp`='"+user.getXp()+"', " +
                "`warnings`='"+user.getWarnings()+"' WHERE `vkId`='"+user.getId()+"';", preparedStatement -> {
            try {
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                BuseAPI.getBuseLogger().error(e);
            }
        });
    }

}
