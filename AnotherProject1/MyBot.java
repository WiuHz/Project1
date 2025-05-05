package AnotherProject1;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatPermissions;
import org.telegram.telegrambots.meta.api.methods.groupadministration.PromoteChatMember;
import java.io.File;

public class MyBot extends TelegramLongPollingBot{
    private  String botUsername;
    private  String botToken;
    
    public MyBot(String botUsername, String botToken){
        this.botUsername = botUsername;
        this.botToken = botToken;
    }
    
    public void setBotUsername(String botUsername){
        this.botUsername = "7472332445";
    }

    @Override
    public String getBotUsername(){
        return botUsername;
    }
    
    public void setBotToken(String botToken){
        this.botToken = "AAHENKQzvuxdArKWBKPBw4dUHhnkTcMjQds";
    }

    @Override
    public String getBotToken(){
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        User user = message.getFrom();

        System.out.println("User: " + user.getUserName() + " sent message: " + message.getText());
    }

    public void sendMessage(String chatid, String mes, String replyMarkup){
        SendMessage message_sending = new SendMessage();
        message_sending.setChatId(chatid);
        message_sending.setText(mes);
        message_sending.setReplyMarkup(replyMarkup);
        try{
            execute(message_sending);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendPhoto(String chatid, String photo, String caption){
        SendPhoto send = new SendPhoto();
        send.setChatId(chatid); 
        send.setPhoto(new InputFile(new File(photo)));
        send.setCaption(caption);
        try{
            execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendVideo(String chatid, String video, String caption){
        SendVideo send = new SendVideo();
        send.setChatId(chatid); 
        send.setVideo(new InputFile(new File(video)));
        send.setCaption(caption);
        try{
            execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDocument(String chatid, String document, String caption){
        SendDocument send = new SendDocument();
        send.setChatId(chatid); 
        send.setDocument(new InputFile(new File(document)));
        send.setCaption(caption);
        try{
            execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void editMessage(String chatid, String mesID, String new_mes){
        EditMessage edit = new EditMessage();
        edit.setChatId(chatid);
        edit.setMessageId(Integer.parseInt(mesID));
        edit.setText(new_mes);
        try{
            execute(edit);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(String chatid, String mesID){
        DeleteMessage delete = new DeleteMessage();
        delete.setChatId(chatid);
        delete.setMessageId(Integer.parseInt(mesID));
        try{
            execute(delete);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void forwardMessage(String chatid, String fromChatID, String mesID){
        ForwardMessage forward = new ForwardMessage();
        forward.setChatId(chatid);
        forward.setFromChatId(fromChatID);
        forward.setMessageId(Integer.parseInt(mesID));
        try{
            execute(forward);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void copyMessage(String chatid, String fromChatID, String mesID){
        CopyMessage copy = new CopyMessage();
        copy.setChatId(chatid);
        copy.setFromChatId(fromChatID);
        copy.setMessageId(Integer.parseInt(mesID));
        try{
            execute(copyMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void getChat(String chatid){
        GetChat get = new GetChat();
        get.setChatId(chatid);
        try{
            Chat chat = execute(get);
            System.out.println("Group title: " + chat.getTitle());
            System.out.println("Group type: " + chat.getType());
        }
        catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
    public void getChatMember(String chatid, Long userID){
        GetChatMember getmember = new GetChatMember();
        getmember.setChatId(chatid);
        getmember.setUserID(userID);
        try{
            ChatMember chatMember = execute(getmember);
            System.out.println("Member status: " + chatMember.getStatus());
        }
        catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
    public void kickChatMember(String chatid, Long userID){
        KickChatMember kick= new KickChatMember();
        kick.setChatId(chatid);
        kick.setUserID(userID);
        try{
            execute(kickChatMember);
            System.out.println("The member have been kicked!");
        }
        catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
    public void restrictChatMember(String chatid, Long userID){
        RestrictChatMember restrict = new RestrictChatMember();
        restrict.setChatId(chatid);
        restrict.setUserID(userID);

        ChatPermissions permissions = new ChatPermissions();
        permissions.setCanSendMessages(false);
        permissions.setCanSendMediaMessages(false);
        permissions.setCanSendPolls(false);
        permissions.setCanSendOtherMessages(false);
        permissions.setCanAddWebPagePreviews(false);
        permissions.setCanChangeInfo(false);
        permissions.setCanInviteUsers(false);
        permissions.setCanPinMessages(false);

        restrictChatMember.setPermissions(permissions);

        try{
            execute(restrictChatMember);
            System.out.println("The user with ID " + userID + " has been restricted!");
        }
        catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
}