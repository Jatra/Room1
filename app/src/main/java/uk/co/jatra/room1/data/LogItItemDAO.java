package uk.co.jatra.room1.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by tim on 04/06/2017.
 */
@Dao
public interface LogItItemDAO {
    @Query("SELECT * FROM item")
    Flowable<List<LogItItem>> getAll();

    @Query("SELECT * FROM item WHERE id = :id")
    Flowable<List<LogItItem>> loadAllById(int id);

    @Query("DELETE FROM item WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM item WHERE id IN (:ids)")
    Flowable<List<LogItItem>> loadAllByIds(int[] ids);

    @Query("SELECT * FROM item WHERE description LIKE :description LIMIT 1")
    Flowable<List<LogItItem>> findByDescription(String description);

    @Insert
    void insertAll(LogItItem... logItItems);

    @Insert
    void insert(LogItItem logItItem);

    @Delete
    void delete(LogItItem logItItem);


}
