package ru.spring.dao.interfaces;

import ru.spring.dao.objects.MP3;

import java.util.List;
import java.util.Map;

public interface MP3Dao {
    int insert(MP3 mp3);
    void insertList(List<MP3> mp3List);
    void delete(int id);
    void delete(MP3 mp3);
    MP3 getMP3ById(int id);
    List<MP3> getMP3ListByName(String name);
    List<MP3> getMP3ListByAuthor(String name);
    int getMP3Count();
    Map<String,Integer> getStat();
    int[] bathInsert(List<MP3> mp3List);
}
