package ru.javawebinar.topjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Menu;
import ru.javawebinar.topjava.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
    @Override
    @Query("SELECT DISTINCT r FROM Restaurant r LEFT JOIN FETCH r.menu m LEFT JOIN FETCH m.dishes ORDER BY r.name")
    List<Restaurant> findAll();

    @Override
    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.menu m LEFT JOIN FETCH m.dishes WHERE r.name like ?1")
    Optional<Restaurant> findById(String name);

    @Query("SELECT r FROM Restaurant r WHERE r.name=?1")
    Optional<Restaurant> findOne(String name);

    @Modifying
    @Transactional
    @Query("UPDATE Restaurant r SET r.name=?2 WHERE r.name=?1")
    int update(String oldName, String newName);

    @Modifying
    @Transactional
    @Query("DELETE FROM Restaurant r WHERE r.name like ?1")
    int delete(String name);

    @Query("SELECT m FROM Menu m WHERE m.restaurant.name like ?1 AND m.date=CURRENT_DATE")
    Menu getMenu(String name);
}
