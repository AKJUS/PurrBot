package com.andre601.purrbot.util;

import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.util.constants.IDs;
import com.andre601.purrbot.core.PurrBot;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

public class VoteUtil {

    /**
     * Performs an action when the vote listener received a webhook message from the bot-page.
     *
     * @param botId
     *        The ID of the bot, provided through the webhook message.
     * @param voterId
     *        The ID of the voter, provided through the webhook message.
     * @param isWeekend
     *        A boolean for if it's actually weekend, provided through the webhook message.
     */
    public static void voteAction(String botId, String voterId, boolean isWeekend){
        if(!botId.equals(IDs.PURR)) return;

        Message msg;
        WebhookClient webhookClient = PurrBot.getWebhookClient(
                PurrBot.file.getItem("config", "vote-webhook")
        );
        Guild guild = ReadyListener.getShardManager().getGuildById(IDs.GUILD);
        if(voterIsInGuild(guild, voterId)){
            Role role = guild.getRoleById(IDs.VOTE_ROLE);
            Member member = guild.getMemberById(voterId);
            msg = new MessageBuilder()
                    .append(String.format(
                            "%s has voted for the bot! Thank you! \uD83C\uDF89\n" +
                            "Vote too on <https://discordbots.org/bot/425382319449309197>!",
                            member.getAsMention()
                    ))
                    .build();
            if(!guild.getMemberById(voterId).getRoles().contains(role)) {
                guild.getController().addRolesToMember(member, role).queue();
            }

            BufferedImage image = ImageUtil.createVoteImage(member.getUser(), isWeekend);

            if(image == null){
                webhookClient.send(msg);
                webhookClient.close();
                return;
            }

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.setUseCache(false);
                ImageIO.write(image, "png", baos);

                webhookClient.send(new WebhookMessageBuilder(msg)
                        .addFile(String.format(
                        "vote_%s.png",
                        voterId
                ), baos.toByteArray())
                        .build()
                );
                webhookClient.close();
            }catch (IOException ex){
                webhookClient.send(msg);
                webhookClient.close();
            }


        }else{
            msg = new MessageBuilder()
                    .append(
                            "A anonymous person has voted for the bot!\n" +
                            "Vote too on <https://discordbots.org/bot/425382319449309197>!"
                    )
                    .build();
            webhookClient.send(msg);
            webhookClient.close();
        }
    }

    /**
     * Checks if the member is on the support-guild.
     *
     * @param  userId
     *         The ID of the user.
     *
     * @return True if the user is not null (on the guild).
     */
    private static boolean voterIsInGuild(Guild guild, String userId){
        return guild.getMemberById(userId) != null;
    }

}