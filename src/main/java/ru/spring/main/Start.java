package ru.spring.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.spring.dao.impl.SQLiteDAO;
import ru.spring.dao.objects.MP3;

public class Start {
    public static void main(String[] args) {
        MP3 mp3=new MP3();
        mp3.setName("n4");
        mp3.setAuthor("a4");

        ApplicationContext context=new ClassPathXmlApplicationContext("context.xml");
        SQLiteDAO sqLiteDAO=(SQLiteDAO) context.getBean("SQLiteDAO");

        sqLiteDAO.insert(mp3);
    }
}
