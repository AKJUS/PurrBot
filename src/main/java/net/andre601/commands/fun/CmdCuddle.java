package net.andre601.commands.fun;

import net.andre601.commands.Command;
import net.andre601.commands.server.CmdPrefix;
import net.andre601.util.messagehandling.EmbedUtil;
import net.andre601.util.HttpUtil;
import net.andre601.util.PermUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

public class CmdCuddle implements Command{

    //  This sends a message, if it is executed.
    public void usage(Message msg){
        msg.getTextChannel().sendMessage(String.format(
                        "%s Please mention a user at the end of the command to cuddle with!\n" +
                        "Example: `%scuddle @*Purr*#6875`",
                msg.getAuthor().getAsMention(),
                CmdPrefix.getPrefix(msg.getGuild())
        )).queue();
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();

        //  Permission-checks for write and embed links-permission.
        if (!PermUtil.canWrite(tc))
            return;

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        //  Send usage-message defined above, if you only run the command with no args.
        if(args.length < 1){
            usage(e.getMessage());
            return;
        }

        List<User> user = msg.getMentionedUsers();

        if(user.size() == 1){
            User u = user.get(0);
            //  mentioned user = own user -> send message, add reaction and return.
            if(u == msg.getJDA().getSelfUser()){
                if(PermUtil.canReact(tc))
                    e.getMessage().addReaction("❤").queue();

                tc.sendMessage(String.format("%s \\*Enjoys the cuddle*",
                        msg.getMember().getAsMention())).queue();
                return;
            }

            //  mentioned user = author of the message -> Send message and return.
            if(u == msg.getAuthor()){
                tc.sendMessage("Do you have no one to cuddle with?").queue();
                return;
            }
            String name = u.getAsMention();
            tc.sendMessage(String.format(
                    "%s cuddles with you %s",
                    msg.getMember().getEffectiveName(),
                    name
            )).queue(message -> {
                try{
                    message.editMessage(
                            EmbedUtil.getEmbed().setImage(HttpUtil.getCuddle()).build()
                    ).queue();
                }catch (Exception ignored){
                }
            });

        }else{
            String users = user.stream().map(User::getAsMention).collect(Collectors.joining(", "));
            tc.sendMessage(String.format(
                    "%s cuddles with you %s",
                    msg.getMember().getEffectiveName(),
                    users
            )).queue(message -> {
                try{
                    message.editMessage(
                            EmbedUtil.getEmbed().setImage(HttpUtil.getCuddle()).build()
                    ).queue();
                }catch (Exception ignored){
                }
            });
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return null;
    }
}
