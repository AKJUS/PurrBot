package net.andre601.util;

import net.andre601.core.PurrBotMain;
import net.andre601.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class ImageUtil {

    public static final String[] UA = {"User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"};

    private static BufferedImage getUserIcon(User user){

        BufferedImage icon = null;

        try{
            URL serverIcon = new URL(user.getEffectiveAvatarUrl());
            URLConnection connection = serverIcon.openConnection();
            connection.setRequestProperty(UA[0], UA[1]);
            connection.connect();
            icon = ImageIO.read(connection.getInputStream());
        }catch (Exception ignored){
        }
        return icon;
    }

    public static void createWelcomeImg(User user, Guild g, TextChannel tc, Message msg, String imageType){

        //  Saving the userIcon/avatar as a Buffered image
        BufferedImage u = getUserIcon(user);

        try{
            String number = String.valueOf(1);
            switch (imageType){
                case "purr":
                    number = String.valueOf(1);
                    break;
                case "gradient":
                    number = String.valueOf(2);
                    break;
                case "landscape":
                    number = String.valueOf(3);
                    break;
                case "random":
                    number = String.valueOf(PurrBotMain.getRandom().nextInt(3));
                    break;
            }

            BufferedImage layer = ImageIO.read(new File("img/welcome_layer" + number + ".png"));

            BufferedImage bg = ImageIO.read(new File("img/welcome_bg.png"));
            BufferedImage image = new BufferedImage(bg.getWidth(), bg.getHeight(), bg.getType());
            Graphics2D img = image.createGraphics();

            //  Adding the different images (background -> User-Avatar -> actual image)
            img.drawImage(bg, 0, 0, null);
            img.drawImage(u, 5, 5, 290, 290, null);
            img.drawImage(layer, 0, 0, null);

            //  Creating the font for the custom text.
            Font text = new Font("Arial", Font.PLAIN, 60);
            img.setColor(Color.WHITE);
            img.setFont(text);

            //  Setting the actual text. \n is (sadly) not supported, so we have to make each new line seperate.
            img.drawString("Welcome",320, 100);
            img.drawString(MessageUtil.getTag(user),320, 175);
            img.drawString(String.format(
                    "You are user #%s",
                    g.getMembers().size()
            ),320, 250);

            img.dispose();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.setUseCache(false);
            ImageIO.write(image, "png", stream);

            //  Finally sending the image. I use the user-id as image-name (prevents issues with non-UTF-8 symbols...)

            tc.sendFile(stream.toByteArray(), String.format(
                    "%s.png",
                    user.getId()
            ), msg).queue();

            //  We just ignore the caused exception.
        }catch (IOException ignored){
        }
    }

    public static void createImg(String url, Message msg){
        String imgName = MessageFormat.format(
                "FavoriteImage({0}).png",
                msg.getAuthor().getId());
        TextChannel tc = msg.getTextChannel();

        try{
            URL imgURL = new URL(url);
            URLConnection connection = imgURL.openConnection();
            connection.setRequestProperty(UA[0], UA[1]);
            if(connection.getContentType().equals("image/gif"))
                imgName = MessageFormat.format(
                        "FavoriteImage({0}).gif",
                        msg.getAuthor().getId()
                );
            Message newMsg = new MessageBuilder()
                    .append(MessageFormat.format(
                            "Image requested by {0}!",
                            msg.getAuthor().getAsMention()
                    ))
                    .build();
            try{
                final String finalImgName = imgName;
                tc.sendFile(connection.getInputStream(), finalImgName, newMsg).queue();
            }catch (Exception ignored){
                tc.sendMessage(String.format("%s There was an issue with sending the image :(")).queue();
            }
        }catch (Exception ignored){
            tc.sendMessage(String.format(
                    "%s You need to provide a valid URL!",
                    msg.getAuthor().getAsMention()
            )).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
        }
    }

}
