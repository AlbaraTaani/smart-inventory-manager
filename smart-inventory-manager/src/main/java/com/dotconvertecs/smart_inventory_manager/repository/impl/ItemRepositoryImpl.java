package com.dotconvertecs.smart_inventory_manager.repository.impl;


import com.dotconvertecs.smart_inventory_manager.model.entity.Item;
import com.dotconvertecs.smart_inventory_manager.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.springframework.jdbc.core.JdbcOperationsExtensionsKt.query;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ItemRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper to convert a ResultSet row into an Item
    private static final RowMapper<Item> ITEM_ROW_MAPPER = (rs, rowNum) -> {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setName(rs.getString("name"));
        item.setDescription(rs.getString("description"));
        item.setQuantity(rs.getInt("quantity"));
        item.setPrice(rs.getDouble("price"));
        return item;
    };

    public List<Item> findAll() {
        String sql = "SELECT id, name, description, quantity, price FROM items";
        return jdbcTemplate.query(sql, ITEM_ROW_MAPPER);
    }

    public Optional<Item> findById(Long id) {
        String sql = "SELECT id, name, description, quantity, price FROM items WHERE id = ?";
        try {
            Item item = jdbcTemplate.queryForObject(sql, ITEM_ROW_MAPPER, id);
            return Optional.ofNullable(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int create(Item item) {
        String sql = "INSERT INTO items (name, description, quantity, price) VALUES (?, ?, ?, ?)";
        // Use KeyHolder to get generated ID if needed
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getPrice());
            return ps;
        }, keyHolder);
        item.setId(keyHolder.getKey().longValue());
        return keyHolder.getKey().intValue();
    }

    public int update(Item item) {
        String sql = "UPDATE items SET name = ?, description = ?, quantity = ?, price = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                item.getName(),
                item.getDescription(),
                item.getQuantity(),
                item.getPrice(),
                item.getId());
    }


    @Override
    public Item save(Item item) {
        String sql = "INSERT INTO items (name, description, quantity, price) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getPrice());
            return ps;
        }, keyHolder);

        Number generated = keyHolder.getKey();
        if (generated != null) {
            item.setId(generated.longValue());
        }
        return item;
    }

    public int delete(Long id) {
        String sql = "DELETE FROM items WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // Custom query for low-stock items
    public List<Item> findLowStock(int threshold) {
        String sql = "SELECT id, name, description, quantity, price FROM items WHERE quantity <= ?";
        return jdbcTemplate.query(sql, ITEM_ROW_MAPPER, threshold);
    }
}

