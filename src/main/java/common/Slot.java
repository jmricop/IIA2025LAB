/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

import java.util.Queue;

/**
 *
 * @author apolo
 */
public class Slot {
    
    private Queue<Message> messages;

    public Queue<Message> getMessages() {
        return messages;
    }

    public void setMessages(Queue<Message> messages) {
        this.messages = messages;
    }

    public Slot(Queue<Message> messages) {
        this.messages = messages;
    }
    
    public void addMessage(Message msg){
        
        messages.add(msg);
        
    }
    
    public Message getFirstMessage(){
        
        return messages.remove();
    }
    
    public boolean isEmpty(){return messages.isEmpty();}
    public int nMessages(){return messages.size();}
    
}
