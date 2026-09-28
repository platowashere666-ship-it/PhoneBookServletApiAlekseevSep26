package ru.academits.alekseev.phonebookservletapi.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ru.academits.alekseev.phonebookservletapi.data.Contact;
import ru.academits.alekseev.phonebookservletapi.data.ContactInMemoryRepository;
import ru.academits.alekseev.phonebookservletapi.data.ContactRepository;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serial;
import java.util.List;

@WebServlet(urlPatterns = {"/api/contacts", "/api/contacts/*"})
public class ContactServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 28L;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ContactRepository contactRepository = new ContactInMemoryRepository();

        String searchQuery = req.getParameter("searchText");

        if (searchQuery != null) {
            List<Contact> foundContacts = contactRepository.search(searchQuery);
            mapper.writeValue(resp.getOutputStream(), foundContacts);
        } else {
            mapper.writeValue(resp.getWriter(), contactRepository.getAll());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ContactRepository contactRepository = new ContactInMemoryRepository();
        Contact contact = mapper.readValue(req.getReader(), Contact.class);

        if (contact == null || contact.getSurname().isBlank() ||
                contact.getName().isBlank() || contact.getPhone().isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), "Error: all fields are required");
        } else {

            Contact savedContact = contactRepository.create(
                    contact.getSurname().trim(),
                    contact.getName().trim(),
                    contact.getPhone().trim()
            );

            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), savedContact);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), "Error: contact id is required");
        } else {
            int id = Integer.parseInt(pathInfo.substring(1));
            Contact contact = mapper.readValue(req.getReader(), Contact.class);

            if (contact == null || contact.getSurname().isBlank() ||
                    contact.getName().isBlank() || contact.getPhone().isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(resp.getWriter(), "Error: all fields are required");
            } else {
                contact.setId(id);

                ContactRepository contactRepository = new ContactInMemoryRepository();
                contactRepository.update(contact);

                mapper.writeValue(resp.getWriter(), contact);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), "Error: contact id is required");
        } else {
            int id = Integer.parseInt(pathInfo.substring(1));

            ContactRepository contactRepository = new ContactInMemoryRepository();
            contactRepository.delete(id);

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }
}