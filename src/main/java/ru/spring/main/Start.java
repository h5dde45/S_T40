package ru.spring.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.spring.dao.interfaces.MP3Dao;
import ru.spring.dao.objects.MP3;

import java.util.ArrayList;
import java.util.List;

public class Start {
    public static void main(String[] args) {
        List<MP3> mp3List=new ArrayList<MP3>();
        MP3 mp3=new MP3();
mp3.setId(15);
        mp3.setName("n5");
        mp3.setAuthor("a5");
        MP3 mp4=new MP3();
        mp4.setName("n4");
        mp4.setAuthor("a4");
        mp3List.add(mp3);
        mp3List.add(mp4);

        ApplicationContext context=new ClassPathXmlApplicationContext("context.xml");
        MP3Dao sqLiteDAO=(MP3Dao) context.getBean("SQLiteDAO");

        System.out.println(sqLiteDAO.getMP3Count());
    }

}
