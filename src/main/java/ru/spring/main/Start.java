package ru.spring.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.spring.dao.interfaces.MP3Dao;
import ru.spring.dao.objects.Author;
import ru.spring.dao.objects.MP3;

import java.util.ArrayList;
import java.util.List;

public class Start {
    public static void main(String[] args) {
        List<MP3> mp3List = new ArrayList<MP3>();
        MP3 mp3 = new MP3();
        mp3.setName("n7");

        Author author=new Author();
        author.setName("a7");

        mp3.setAuthor(author);

        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        MP3Dao sqLiteDAO = (MP3Dao) context.getBean("SQLiteDAO");

        System.out.println(sqLiteDAO.insertMP3(mp3));
    }

}
