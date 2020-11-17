package ru.javawebinar.topjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Menu;
import ru.javawebinar.topjava.model.Vote;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface VoteRepository extends JpaRepository<Vote, Integer> {

    @Query("SELECT v FROM Vote v JOIN FETCH v.menu m JOIN FETCH m.dishes JOIN FETCH m.restaurant WHERE v.user.id=?1 AND m.date=CURRENT_DATE")
    Vote findByUser(int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Vote v WHERE v.user.id=?1 AND (SELECT m.date FROM Menu m WHERE m=v.menu)=CURRENT_DATE")
    void deleteByUser(int userId);

    @Query("SELECT v FROM Vote v JOIN FETCH v.menu m JOIN FETCH m.dishes d JOIN FETCH m.restaurant WHERE m.date>=?1")
    List<Vote> findAll(LocalDate date);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.menu.restaurant.name like ?1 AND v.menu.date=CURRENT_DATE")
    int getCount(String restaurant);

    @Query("SELECT DISTINCT m AS key, COUNT(v) AS value FROM Menu m LEFT JOIN Vote v ON v.menu=m WHERE m.date>=?1 GROUP BY m.id")
    Set<Map.Entry<Menu, Long>> getEntrySet(LocalDate date);

    default Map<Menu, Long> getVotesMap(LocalDate date) {
        return getEntrySet(date).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
    }
}
