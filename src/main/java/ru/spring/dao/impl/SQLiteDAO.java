package ru.spring.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.spring.dao.interfaces.MP3Dao;
import ru.spring.dao.objects.MP3;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class SQLiteDAO implements MP3Dao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(MP3 mp3) {
        String sql = "insert into mp3 (name,author) values(:name,:author)";
        MapSqlParameterSource parameters =new MapSqlParameterSource();
        parameters.addValue("name",mp3.getName());
        parameters.addValue("author",mp3.getAuthor());

        jdbcTemplate.update(sql,parameters);
    }

    public void insertList(List<MP3> mp3List) {
        for (MP3 mp3 : mp3List) {
            insert(mp3);
        }
    }

    public void delete(int id) {
        String sql = "delete from mp3 where id=:id";
        MapSqlParameterSource parameters =new MapSqlParameterSource();
        parameters.addValue("id",id);
        jdbcTemplate.update(sql, parameters);
    }

    public void delete(MP3 mp3) {
        delete(mp3.getId());
    }

    public MP3 getMP3ById(int id) {
        String sql = "SELECT * from mp3 where id=:id";
        MapSqlParameterSource parameters =new MapSqlParameterSource();
        parameters.addValue("id",id);
        return jdbcTemplate.queryForObject(sql, parameters,new MP3RowMapper());
    }

    public List<MP3> getMP3ListByName(String name) {
        String sql = "SELECT * from mp3 where LOWER(name) LIKE :name";
        MapSqlParameterSource parameters =new MapSqlParameterSource();
        parameters.addValue("name","%"+name.toLowerCase()+"%");
        return jdbcTemplate.query(sql, parameters,new MP3RowMapper());
    }

    public List<MP3> getMP3ListByAuthor(String author) {
        String sql = "SELECT * from mp3 where LOWER(author) LIKE :author";
        MapSqlParameterSource parameters =new MapSqlParameterSource();
        parameters.addValue("author","%"+author.toLowerCase()+"%");
        return jdbcTemplate.query(sql, parameters,new MP3RowMapper());
    }

    public int getMP3Count(){
        String sql="select count(*) from mp3";
        return jdbcTemplate.getJdbcOperations().queryForObject(sql,Integer.class);
    }

    private static final class MP3RowMapper implements RowMapper<MP3>{

        public MP3 mapRow(ResultSet resultSet, int i) throws SQLException {
            MP3 mp3=new MP3();
            mp3.setId(resultSet.getInt("id"));
            mp3.setName(resultSet.getString("name"));
            mp3.setAuthor(resultSet.getString("author"));
            return mp3;
        }
    }
}
