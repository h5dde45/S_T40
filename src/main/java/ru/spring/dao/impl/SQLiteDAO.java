package ru.spring.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.spring.dao.interfaces.MP3Dao;
import ru.spring.dao.objects.MP3;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SQLiteDAO implements MP3Dao {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private DataSource dataSource;
    private SimpleJdbcInsert insertMP3;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.dataSource = dataSource;
        this.insertMP3 = new SimpleJdbcInsert(dataSource).withTableName("mp3").
                usingColumns("name", "author");
    }

    public int insert(MP3 mp3) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", mp3.getName());
        parameters.addValue("author", mp3.getAuthor());

        return insertMP3.execute(parameters);
    }

    public int[] bathInsert(List<MP3> mp3List) {
        String sql = "insert into mp3 (name,author) VALUES (:name, :author)";
        SqlParameterSource[] bath = SqlParameterSourceUtils.createBatch(mp3List.toArray());
        int[] updateCounts = jdbcTemplate.batchUpdate(sql, bath);
        return updateCounts;
    }

    public void insertList(List<MP3> mp3List) {
        for (MP3 mp3 : mp3List) {
            insert(mp3);
        }
    }

    public void delete(int id) {
        String sql = "delete from mp3 where id=:id";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        jdbcTemplate.update(sql, parameters);
    }

    public void delete(MP3 mp3) {
        delete(mp3.getId());
    }

    public MP3 getMP3ById(int id) {
        String sql = "SELECT * from mp3 where id=:id";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        return jdbcTemplate.queryForObject(sql, parameters, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByName(String name) {
        String sql = "SELECT * from mp3 where LOWER(name) LIKE :name";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", "%" + name.toLowerCase() + "%");
        return jdbcTemplate.query(sql, parameters, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByAuthor(String author) {
        String sql = "SELECT * from mp3 where LOWER(author) LIKE :author";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("author", "%" + author.toLowerCase() + "%");
        return jdbcTemplate.query(sql, parameters, new MP3RowMapper());
    }

    public int getMP3Count() {
        String sql = "select count(*) from mp3";
        return jdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class);
    }

    @Override
    public Map<String, Integer> getStat() {
        String sql = "select author, count(*) as count from mp3 group by author";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String, Integer> map = new HashMap<>();
                while (resultSet.next()) {
                    String author = resultSet.getString("author");
                    int count = resultSet.getInt("count");
                    map.put(author, count);
                }
                return map;
            }
        });
    }

    private static final class MP3RowMapper implements RowMapper<MP3> {

        public MP3 mapRow(ResultSet resultSet, int i) throws SQLException {
            MP3 mp3 = new MP3();
            mp3.setId(resultSet.getInt("id"));
            mp3.setName(resultSet.getString("name"));
            mp3.setAuthor(resultSet.getString("author"));
            return mp3;
        }
    }
}
