package volunteers.un.unitednationsvolunteers.Models;



public class ChatUser {
    public String name;
    public String email;
    public String avata;
    public Status status;
    public String profileUrl;
    public String phone;
    public Message message;


    public ChatUser(){
        status = new Status();
        message = new Message();
        status.isOnline = false;
        status.timestamp = 0;
        profileUrl = "default";
        this.phone="017";
        message.idReceiver = "0";
        message.idSender = "0";
        message.text = "";
        message.timestamp = 0;
    }

    public ChatUser(String name, String email, String profileUrl, String phone, String address) {
        this.name =name;
        this.email = email;
    }
}
