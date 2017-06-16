package ru.spring.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.spring.dao.interfaces.MP3Dao;
import ru.spring.dao.objects.MP3;

import javax.sql.DataSource;
import java.util.List;

@Component
public class SQLiteDAO implements MP3Dao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
       jdbcTemplate=new JdbcTemplate(dataSource);
    }

    public void insert(MP3 mp3) {
        String sql="insert into mp3 (name,author) values(?,?)";
        jdbcTemplate.update(sql,
                new Object[]{mp3.getName(),mp3.getAuthor()});
    }

    public void delete(MP3 mp3) {

    }

    public MP3 getMP3ById(int id) {
        return null;
    }

    public List<MP3> getMP3ListByName(String name) {
        return null;
    }

    public List<MP3> getMP3ListByAuthor(String name) {
        return null;
    }
}
