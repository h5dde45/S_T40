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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.spring.dao.interfaces.MP3Dao;
import ru.spring.dao.objects.Author;
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
    private SimpleJdbcInsert insertMP3;

    private static final String mp3Table = "mp3";
    private static final String mp3View = "mp3_view";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertMP3 = new SimpleJdbcInsert(dataSource).withTableName("mp3").
                usingColumns("name", "author");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int insertMP3(MP3 mp3) {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());

        int author_id=insertAuthor(mp3.getAuthor());

        String sqlMP3 = "insert into mp3 (name, author_Id) values (:name, :authorId)";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", mp3.getName());
        parameters.addValue("authorId", author_id);

        return jdbcTemplate.update(sqlMP3,parameters);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int insertAuthor(Author author) {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        String sqlAuthor = "insert into author (name) values (:authorName)";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("authorName",author.getName());
        KeyHolder keyHolder=new GeneratedKeyHolder();
        jdbcTemplate.update(sqlAuthor,parameters,keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int[] bathInsert(List<MP3> mp3List) {
        String sql = "insert into mp3 (name,author) VALUES (:name, :author)";
        SqlParameterSource[] bath = SqlParameterSourceUtils.createBatch(mp3List.toArray());
        int[] updateCounts = jdbcTemplate.batchUpdate(sql, bath);
        return updateCounts;
    }

    public void insertList(List<MP3> mp3List) {
        for (MP3 mp3 : mp3List) {
            insertMP3(mp3);
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
        String sql = "SELECT * from " + mp3View + " where mp3_id=:mp3_id";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("mp3_id", id);
        return jdbcTemplate.queryForObject(sql, parameters, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByName(String name) {
        String sql = "SELECT * from " + mp3View + " where LOWER(mp3_name) LIKE :mp3_name";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("mp3_name", "%" + name.toLowerCase() + "%");
        return jdbcTemplate.query(sql, parameters, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByAuthor(String author) {
        String sql = "SELECT * from " + mp3View + " where LOWER(author_name) LIKE :author_name";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("author_name", "%" + author.toLowerCase() + "%");
        return jdbcTemplate.query(sql, parameters, new MP3RowMapper());
    }

    public int getMP3Count() {
        String sql = "select count(*) from " + mp3Table;
        return jdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class);
    }

    @Override
    public Map<String, Integer> getStat() {
        String sql = "select author_name, count(*) as count from " + mp3View + " group by author_name";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String, Integer> map = new HashMap<>();
                while (resultSet.next()) {
                    String author = resultSet.getString("author_name");
                    int count = resultSet.getInt("count");
                    map.put(author, count);
                }
                return map;
            }
        });
    }

    private static final class MP3RowMapper implements RowMapper<MP3> {

        public MP3 mapRow(ResultSet resultSet, int i) throws SQLException {
            Author author = new Author();
            author.setId(resultSet.getInt("author_id"));
            author.setName(resultSet.getString("author_name"));

            MP3 mp3 = new MP3();
            mp3.setId(resultSet.getInt("mp3_id"));
            mp3.setName(resultSet.getString("mp3_name"));
            mp3.setAuthor(author);
            return mp3;
        }
    }
}
