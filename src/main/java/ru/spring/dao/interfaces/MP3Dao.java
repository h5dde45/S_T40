package ru.spring.dao.interfaces;

import ru.spring.dao.objects.MP3;

import java.util.List;

public interface MP3Dao {
    void insert(MP3 mp3);
    void delete(MP3 mp3);
    MP3 getMP3ById(int id);
    List<MP3> getMP3ListByName(String name);
    List<MP3> getMP3ListByAuthor(String name);
}
