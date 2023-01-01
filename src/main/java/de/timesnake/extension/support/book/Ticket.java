/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.support.book;

import de.timesnake.database.util.support.DbTicket;
import de.timesnake.library.basic.util.Status;

public class Ticket {

    private final DbTicket database;

    private final Integer id;
    private final String name;
    private final String uuid;
    private Status.Ticket status;
    private String message;
    private String answer;

    public Ticket(DbTicket database, Integer id, String name, String uuid, Status.Ticket status, String message,
                  String answer) {
        this.database = database;
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.status = status;
        this.message = message;
        this.answer = answer;
    }

    public Integer getId() {
        return id;
    }

    public DbTicket getDatabase() {
        return database;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public Status.Ticket getStatus() {
        return status;
    }

    public void setStatus(Status.Ticket status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
