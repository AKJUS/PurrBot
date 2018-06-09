package net.andre601.commands.server;

import net.andre601.commands.Command;
import net.andre601.core.PurrBotMain;
import net.andre601.util.DBUtil;
import net.andre601.util.messagehandling.EmbedUtil;
import net.andre601.util.PermUtil;
import net.andre601.util.Static;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CmdPrefix implements Command{

    private static Map<Guild, String> guildPrefix = new HashMap<>();

    public static String getPrefix(Guild g){

        return DBUtil.getPrefix(g);

        //  If the bot is beta-version -> use Beta-prefix (..)
        // return (PurrBotMain.file.getItem("config", "beta").equalsIgnoreCase("true") ?
        //        Static.BETA_PREFIX : Static.PREFIX);
    }

    public static Guild getGuild(String id, JDA jda){
        return jda.getGuildById(id);
    }

    public void currPrefix(Message msg, Guild g){
        msg.getTextChannel().sendMessage(String.format(
                "%s My prefix in this Discord is `%s`",
                msg.getAuthor().getAsMention(),
                getPrefix(g)
        )).queue();
    }

    public void setPrefix(Message msg, Guild g, String prefix){
        if(prefix.equals(".")){
            msg.getTextChannel().sendMessage(String.format(
                    "%s Why do you want to set the prefix to `.`?\n" +
                    "Use `%sprefix reset` to reset it to the default one.",
                    msg.getAuthor().getAsMention(),
                    getPrefix(g)
            )).queue();
            return;
        }


        // guildPrefix.put(g, prefix);
        DBUtil.setPrefix(prefix, g.getId());

        // save();

        EmbedBuilder prefixSet = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(String.format(
                        "Prefix set to `%s`",
                        prefix
                ))
                .setColor(Color.GREEN);

        msg.getChannel().sendMessage(prefixSet.build()).queue();
    }

    public void resetPrefix(Message msg, Guild g){
        String prefix = DBUtil.getPrefix(g);
        if(prefix.equals(".")){
            msg.getTextChannel().sendMessage(String.format(
                    "%s There is no prefix set for this Guild!\n" +
                    "The default prefix is `.`!",
                    msg.getAuthor().getAsMention()
            )).queue();
        }else {
            DBUtil.resetPrefix(g.getId());
            EmbedBuilder prefixReset = EmbedUtil.getEmbed(msg.getAuthor())
                    .setDescription("Prefix was reset successfully!")
                    .setColor(Color.GREEN);

            msg.getTextChannel().sendMessage(prefixReset.build()).queue();
        }
        /*
        if(guildPrefix.containsKey(g)){

            guildPrefix.remove(g);

            // save();

            EmbedBuilder prefixReset = EmbedUtil.getEmbed(msg.getAuthor())
                    .setDescription("Prefix was reset successfully!")
                    .setColor(Color.GREEN);

            msg.getTextChannel().sendMessage(prefixReset.build()).queue();

        }else{

            msg.getTextChannel().sendMessage(String.format(
                    "%s There is no prefix set for this Guild!",
                    msg.getAuthor().getAsMention()
            )).queue();

        }
        */
    }

    /*
    public void save(){
        File path = new File("guilds");
        if(!path.exists())
            path.mkdir();

        Map<String, String> out = new HashMap<>();
        guildPrefix.forEach((g, p) -> out.put(g.getId(), p));
        try{
            FileOutputStream fos = new FileOutputStream(Static.PREFIX_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        }catch (IOException ignored){
        }
    }

    public static void load(JDA jda){

        File file = new File(Static.PREFIX_FILE);
        if(file.exists()){

            try{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Map<String, String> out = (HashMap<String, String>) ois.readObject();
                ois.close();

                out.forEach((gid, p) -> {
                    Guild g = getGuild(gid, jda);
                    guildPrefix.put(g, p);
                });
            }catch (IOException | ClassNotFoundException ignored){
            }

        }
    }
    */

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        TextChannel tc = e.getTextChannel();
        Message msg = e.getMessage();
        Guild g = e.getGuild();

        if (!PermUtil.canWrite(tc))
            return;

        if(!PermUtil.canSendEmbed(tc)){
            tc.sendMessage("I need the permission, to embed Links in this Channel!").queue();
            if(PermUtil.canReact(tc))
                e.getMessage().addReaction("🚫").queue();

            return;
        }

        if(args.length == 0){
            currPrefix(msg, e.getGuild());
            return;
        }

        switch (args[0].toLowerCase()){

            case "set":
                if(PermUtil.userIsAdmin(msg)){
                    if(args.length == 1){
                        tc.sendMessage(String.format(
                                "%s Please provide a prefix!",
                                e.getAuthor().getAsMention()
                        )).queue();
                        break;
                    }
                    if(args.length >= 2){
                        setPrefix(msg, g, args[1]);
                        break;
                    }
                }else{
                    tc.sendMessage(String.format(
                            "%s You need the `MANAGE_SERVER` permission to use this.",
                            e.getAuthor().getAsMention()
                    )).queue();
                    break;
                }
            case "reset":
                if(PermUtil.userIsAdmin(msg)){
                    resetPrefix(msg, g);
                    break;
                }else{
                    tc.sendMessage(String.format(
                            "%s You need the `MANAGE_SERVER` permission to use this.",
                            e.getAuthor().getAsMention()
                    )).queue();
                    break;
                }
            default:
                currPrefix(msg, g);
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