/*
 * extension-support.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
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
