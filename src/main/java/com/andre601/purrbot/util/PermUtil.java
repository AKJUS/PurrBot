package com.andre601.purrbot.util;

import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class PermUtil {

    /*
     *  Permission-checks (Message)
     */

    //  Checking user for manage server-permission
    public static boolean userIsAdmin(Message msg){
        return PermissionUtil.checkPermission(msg.getMember(), Permission.MANAGE_SERVER);
    }

    //  Check if user is Andre_601#6811 (me)
    public static boolean isCreator(Message msg){
        return msg.getAuthor().getId().equals(IDs.CREATOR);
    }

    /*
     *  Permission-checks (TextChannel)
     */

    //  Check for adding reaction-permission
    public static boolean canReact(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_ADD_REACTION);
    }

    //  Check for embed Links-permission
    public static boolean canSendEmbed(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_EMBED_LINKS);
    }

    //  Check for deleting message-permission
    public static boolean canDeleteMsg(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_MANAGE);
    }

    //  Check for using external emoji-permission
    public static boolean canUseCustomEmojis(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_EXT_EMOJI);
    }

    //  Check for uploading images-permission
    public static boolean canUploadImage(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_ATTACH_FILES);
    }

    //  Check for write message-permission
    public static boolean canWrite(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(),
                Permission.MESSAGE_WRITE);
    }

    public static boolean canRead(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(), Permission.MESSAGE_READ);
    }

    public static boolean canReadHistory(TextChannel tc){
        return PermissionUtil.checkPermission(tc, tc.getGuild().getSelfMember(), Permission.MESSAGE_HISTORY);
    }

    public static boolean isBeta(){
        return PurrBot.file.getItem("config", "beta").equalsIgnoreCase("true");
    }

    public static boolean isBot(Message msg){
        return msg.getAuthor().isBot();
    }

    public static boolean isSelf(Message msg){
        return msg.getAuthor() == msg.getJDA().getSelfUser();
    }

    public static boolean isDM(Message msg){
        return msg.isFromType(ChannelType.PRIVATE);
    }
}
