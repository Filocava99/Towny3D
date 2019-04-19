package it.tigierrei.towny3d.residents;

import java.util.UUID;

public class Resident{

    private String name;
    private UUID uuid;
    private String town;
    private String chat;
    private boolean spyChat;

    public Resident(String name, UUID uuid, String town){
        this.name = name;
        this.uuid = uuid;
        this.town = town;
        this.chat = "GL";
        this.spyChat = false;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getTown() {
        return town;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public boolean isSpyChat() {
        return spyChat;
    }

    public void setSpyChat(boolean spyChat) {
        this.spyChat = spyChat;
    }
}
