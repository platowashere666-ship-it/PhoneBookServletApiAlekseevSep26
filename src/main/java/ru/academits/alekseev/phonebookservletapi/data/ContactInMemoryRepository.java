package ru.academits.alekseev.phonebookservletapi.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ContactInMemoryRepository implements ContactRepository {
    private static final List<Contact> contacts = new ArrayList<>();
    private static final AtomicInteger currentId = new AtomicInteger(1);

    @Override
    public List<Contact> getAll() {
        synchronized (contacts) {
            return contacts;
        }
    }

    @Override
    public Contact create(String surname, String name, String phone) {
        synchronized (contacts) {
            Contact contact = new Contact(currentId.getAndIncrement(), surname, name, phone);
            contacts.add(contact);
            return contact;
        }
    }

    @Override
    public void update(Contact contact) {
        synchronized (contacts) {
            Contact repositoryContact = contacts.stream()
                    .filter(c -> c.getId() == contact.getId())
                    .findFirst()
                    .orElse(null);

            if (repositoryContact == null) {
                throw new IllegalArgumentException("Contact with id = " + contact.getId() + " does not exist");
            }

            repositoryContact.setSurname(contact.getSurname());
            repositoryContact.setName(contact.getName());
            repositoryContact.setPhone(contact.getPhone());
        }
    }

    @Override
    public void delete(int id) {
        synchronized (contacts) {
            contacts.removeIf(c -> c.getId() == id);
        }
    }

    @Override
    public List<Contact> search(String query) {
        synchronized (contacts) {
            if (query.isBlank()) {
                return contacts;
            }

            String searchText = query.toLowerCase().trim();

            return contacts.stream()
                    .filter(c ->
                            (c.getSurname().toLowerCase().contains(searchText)) ||
                                    (c.getName().toLowerCase().contains(searchText)) ||
                                    (c.getPhone().toLowerCase().contains(searchText)))
                    .collect(Collectors.toList());
        }
    }
}
