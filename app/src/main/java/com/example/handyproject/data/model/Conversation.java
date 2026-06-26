package com.example.handyproject.data.model;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class Conversation {
    private String id;
    private List<String> participantIds;
    private Map<String, String> participantNames;
    private String lastMessage;
    private Timestamp lastMessageTimestamp;

    public Conversation() {}

    public String getId()                            { return id; }
    public List<String> getParticipantIds()          { return participantIds; }
    public Map<String, String> getParticipantNames() { return participantNames; }
    public String getLastMessage()                   { return lastMessage; }
    public Timestamp getLastMessageTimestamp()        { return lastMessageTimestamp; }

    public void setId(String id)                                       { this.id = id; }
    public void setParticipantIds(List<String> participantIds)         { this.participantIds = participantIds; }
    public void setParticipantNames(Map<String, String> participantNames) { this.participantNames = participantNames; }
    public void setLastMessage(String lastMessage)                     { this.lastMessage = lastMessage; }
    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) { this.lastMessageTimestamp = lastMessageTimestamp; }
}
