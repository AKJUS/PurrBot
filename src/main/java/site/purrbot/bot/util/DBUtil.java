/*
 * Copyright 2019 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.util;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import site.purrbot.bot.PurrBot;

import java.util.Map;

public class DBUtil {

    private PurrBot bot;

    private final RethinkDB r;
    private Connection connection;

    private String guildTable;
    private String memberTable;

    public DBUtil(PurrBot bot){
        r = RethinkDB.r;
        connection = r.connection()
                .hostname(bot.getgFile().getString("config", "db-ip"))
                .port(28015)
                .db(bot.getgFile().getString("config", "db-name"))
                .connect();

        guildTable  = bot.getgFile().getString("config", "db-guildTable");
        memberTable = bot.getgFile().getString("config", "db-memberTable");
        this.bot = bot;
    }

    /*
     *  Guild Stuff
     */

    private void checkValue(String id, String key, String defaultVal){
        Map guild = r.table(guildTable).get(id).run(connection);

        if(guild == null){
            addGuild(id);
            return;
        }

        if(guild.get(key) == null)
            r.table(guildTable).get(id).update(r.hashMap(key, defaultVal)).run(connection);
    }

    /**
     * Adds the Guilds ID to the database with default values.
     *
     * @param id
     *        The ID of the Guild.
     */
    public void addGuild(String id){
        r.table(guildTable).insert(
                r.array(
                        r.hashMap("id", id)
                                .with("prefix", bot.isBeta() ? ".." : ".")
                                .with("welcome_background", "color_white")
                                .with("welcome_channel", "none")
                                .with("welcome_color", "hex:000000")
                                .with("welcome_icon", "purr")
                                .with("welcome_message", "Welcome {mention}!")
                )
        ).optArg("conflict", "update").run(connection);
    }

    /**
     * Deletes the Guild from the database.
     *
     * @param id
     *        The ID of the Guild.
     */
    public void delGuild(String id){
        Map guild = getGuild(id);

        if(guild == null) return;
        r.table(guildTable).get(id).delete().run(connection);
    }

    private Map getGuild(String id){
        return r.table(guildTable).get(id).run(connection);
    }


    /*
     *  Prefix Stuff
     */

    /**
     * Gets the prefix of a Guild.
     *
     * @param  id
     *         The ID of the Guild.
     *
     * @return The prefix of the Guild.
     */
    public String getPrefix(String id){
        checkValue(id, "prefix", ".");
        Map guild = getGuild(id);

        return guild.get("prefix").toString();
    }

    /**
     * Sets the prefix of a Guild.
     *
     * @param id
     *        The ID of the Guild.
     * @param prefix
     *        The new prefix.
     */
    public void setPrefix(String id, String prefix){
        checkValue(id, "prefix", prefix);

        r.table(guildTable).get(id).update(r.hashMap("prefix", prefix)).run(connection);
    }

    /*
     *  Welcome Stuff
     */

    /**
     * Gets the welcome image of the Guild.
     *
     * @param  id
     *         The ID of the Guild to get the image from.
     *
     * @return The image name.
     */
    public String getWelcomeBg(String id){
        checkValue(id, "welcome_background", "color_white");
        Map guild = getGuild(id);

        return guild.get("welcome_background").toString();
    }

    /**
     * Sets the background for the Guild.
     *
     * @param id
     *        The ID of the Guild.
     * @param background
     *        The background that should be used. A list of images can be found here:
     *        <br>https://github.com/Andre601/PurrBot/wiki/Welcome-Images
     */
    public void setWelcomeBg(String id, String background){
        checkValue(id, "welcome_background", background);

        r.table(guildTable).get(id).update(r.hashMap("welcome_background", background)).run(connection);
    }

    /**
     * Gets the welcome channel of the Guild.
     *
     * @param  id
     *         The ID of the Guild to get the channel from.
     *
     * @return The channels ID as String, or "none" if there is no channel set.
     */
    public String getWelcomeChannel(String id){
        checkValue(id, "welcome_channel", "none");
        Map guild = getGuild(id);

        return guild.get("welcome_channel").toString();
    }

    /**
     * Saves the channels ID for the Guild.
     *
     * @param id
     *        The ID of the Guild.
     * @param channelID
     *        The ID of the channel to be saved.
     */
    public void setWelcomeChannel(String id, String channelID){
        checkValue(id, "welcome_channel", channelID);

        r.table(guildTable).get(id).update(r.hashMap("welcome_channel", channelID)).run(connection);
    }

    /**
     * Gets the images font color of the Guild.
     *
     * @param  id
     *         The ID of the Guild to get the color from.
     *
     * @return The color's type and value in the format {@code type:value}.
     */
    public String getWelcomeColor(String id){
        checkValue(id, "welcome_color", "hex:000000");
        Map guild = getGuild(id);

        return guild.get("welcome_color").toString();
    }

    /**
     * Sets the font color for the Welcome image.
     *
     * @param id
     *        The ID of the Guild.
     * @param color
     *        The color to be saved.
     */
    public void setWelcomeColor(String id, String color){
        checkValue(id, "welcome_color", color);

        r.table(guildTable).get(id).update(r.hashMap("welcome_color", color)).run(connection);
    }

    /**
     * Gets the icon for the welcome image.
     *
     * @param  id
     *         The ID of the guild to get the icon from.
     *
     * @return The saved icon of the Guild.
     */
    public String getWelcomeIcon(String id){
        checkValue(id, "welcome_icon", "purr");
        Map guild = getGuild(id);

        return guild.get("welcome_icon").toString();
    }

    /**
     * Sets the icon for the provided Guild to the provided icon.
     *
     * @param id
     *        the ID of the Guild to set the icon for.
     * @param icon
     *        The icon to set.
     */
    public void setWelcomeIcon(String id, String icon){
        checkValue(id, "welcome_icon", icon);

        r.table(guildTable).get(id).update(r.hashMap("welcome_icon", icon)).run(connection);
    }

    /**
     * Gets the welcome message of the Guild.
     *
     * @param  id
     *         The ID of the Guild to get the message from.
     *
     * @return The message that a user will be greeted with.
     */
    public String getWelcomeMsg(String id){
        checkValue(id, "welcome_message", "Welcome {mention}!");
        Map guild = getGuild(id);

        return guild.get("welcome_message").toString();
    }

    /**
     * Sets the welcome message for the Guild.
     *
     * @param id
     *        The ID of the Guild.
     * @param message
     *        The message to be saved.
     */
    public void setWelcomeMsg(String id, String message){
        checkValue(id, "welcome_message", message);

        r.table(guildTable).get(id).update(r.hashMap("welcome_message", message)).run(connection);
    }

    /*
     *  XP/Level stuff
     */

    /**
     * Returns if the provided ID is saved in the database.
     *
     * @param  id
     *         The ID of the member to check.
     *
     * @return True if the member (ID) is in the database.
     */
    boolean hasMember(String id){
        return getMember(id) != null;
    }

    private Map getMember(String id){
        return r.table(memberTable).get(id).run(connection);
    }

    /**
     * Adds the ID of the member to the database with default values.
     *
     * @param id
     *        The ID of the member.
     */
    void addMember(String id){
        r.table(memberTable).insert(
                r.array(
                        r.hashMap("id", id)
                        .with("xp", 0)
                        .with("level", 0)
                )
        ).optArg("conflict", "update").run(connection);
    }

    /**
     * Gets the current XP a member has.
     *
     * @param  id
     *         The ID of the member to get the XP from.
     *
     * @return The XP of the member.
     */
    public long getXp(String id){
        Map member = getMember(id);

        return (long)member.get("xp");
    }

    /**
     * Sets the XP of the member.
     *
     * @param id
     *        The ID of the member.
     * @param xp
     *        The new XP of the member.
     */
    void setXp(String id, long xp){
        r.table(memberTable).get(id).update(r.hashMap("xp", xp)).run(connection);
    }

    /**
     * Gets the level of a member.
     *
     * @param  id
     *         The ID of the member.
     *
     * @return The Level of the member.
     */
    public long getLevel(String id){
        Map member = getMember(id);

        return (long)member.get("level");
    }

    /**
     * Sets the level of a member.
     *
     * @param id
     *        The ID of the member.
     * @param level
     *        The new Level of the member.
     */
    void setLevel(String id, long level){
        r.table(memberTable).get(id).update(r.hashMap("level", level)).run(connection);
    }
}
