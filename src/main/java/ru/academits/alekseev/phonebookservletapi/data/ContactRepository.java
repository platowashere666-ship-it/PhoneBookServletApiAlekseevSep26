package ru.academits.alekseev.phonebookservletapi.data;

import java.util.List;

public interface ContactRepository {
    List<Contact> getAll();

    void create(Contact contact);

    void update(Contact contact);

    void delete(int id);

    List<Contact> search(String query);
}
