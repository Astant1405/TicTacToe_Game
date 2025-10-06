package ru.school21.TicTacToe.domain.model;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;


@Entity
@Table(name = "Game")
public class Game {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "status")
    private String status = "";

    @Column(name = "field_state", length = 9)
    private String fieldState;

    @ManyToOne
    @JoinColumn(name = "first_person")
    private Person firstPerson;

    @ManyToOne
    @JoinColumn(name = "second_person")
    private Person secondPerson;

    @Column(name = "type")
    private String type;

    @Column(name="time_of_create")
    private ZonedDateTime timeOfCreate;

    @Transient
    private Field field;

    public Game() {
    }

    public Game(UUID id, Field field) {
        this.id = id;
        setField(field);
    }

    public Field getField() {
        if (field == null && fieldState != null) {
            field = deserializeField(fieldState);
        } else if (field == null) {
            field = new Field();
        }
        return field;
    }

    public void setField(Field field) {
        this.field = field;
        this.fieldState = serializeField(field);
    }

    private String serializeField(Field field) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int value = field.getElement(i, j);
                if (value == 1) sb.append('X');
                else if (value == -1) sb.append('O');
                else sb.append('E');
            }
        }
        return sb.toString();
    }

    private Field deserializeField(String fieldState) {
        Field field = new Field();
        if (fieldState != null && fieldState.length() == 9) {
            char[] chars = fieldState.toCharArray();
            for (int i = 0; i < 9; i++) {
                int row = i / 3;
                int col = i % 3;
                char c = chars[i];
                if (c == 'X') field.setElement(row, col, 1);
                else if (c == 'O') field.setElement(row, col, -1);
                else field.setElement(row, col, 0);
            }
        }
        return field;
    }

    public Person getSecondPerson() {
        return secondPerson;
    }

    public void setSecondPerson(Person secondPerson) {
        this.secondPerson = secondPerson;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Person getFirstPerson() {
        return firstPerson;
    }

    public void setFirstPerson(Person firstPerson) {
        this.firstPerson = firstPerson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ZonedDateTime getTimeOfCreate() {
        return timeOfCreate;
    }

    public void setTimeOfCreate(ZonedDateTime timeOfCreate) {
        this.timeOfCreate = timeOfCreate;
    }
}
