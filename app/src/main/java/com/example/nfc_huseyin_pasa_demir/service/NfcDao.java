package com.example.nfc_huseyin_pasa_demir.service;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NfcDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Nfc nfc);

    @Update
    public void updateWords(Nfc... nfcs);

    @Query("DELETE FROM nfc_table")
    void deleteAll();

    @Query("SELECT * from nfc_table ORDER BY first_word ASC")
    List<Nfc> getAllWords();

    @Query("SELECT * FROM nfc_table WHERE first_word LIKE :nfc ")
    public List<Nfc> findWord(String nfc);
}