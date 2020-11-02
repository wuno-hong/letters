package com.example.letter.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Letter {
    @PrimaryKey
    public Long id;
    public Integer sendTag;
    public String sender;
    public Date receiveTime;
    public String content;
    public String addressee;

}
