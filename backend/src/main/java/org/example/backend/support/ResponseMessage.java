package org.example.backend.support;

public class ResponseMessage {

    private String messaggio;

    public ResponseMessage() {
    }

    public ResponseMessage(String messaggio) {
        this.messaggio = messaggio;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
}
