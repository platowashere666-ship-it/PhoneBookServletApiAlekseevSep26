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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/contacts", "/api/contacts/*"})
public class ContactServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 28L;

    private static final ObjectMapper mapper = new ObjectMapper();

    private void setJsonHeaders(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private int getIdFromPath(String pathInfo) {
        try {
            if (pathInfo == null || pathInfo.length() <= 1) {
                return -1;
            }

            return Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void writeSuccess(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", message);

        mapper.writeValue(resp.getWriter(), body);
    }

    private void writeError(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", message);

        mapper.writeValue(resp.getWriter(), body);
    }

    private boolean hasErrors(HttpServletResponse resp, Contact contact) throws IOException {
        if (contact == null) {
            writeError(resp, "Error: contact is null");
            return true;
        }

        if (contact.getSurname().isBlank()) {
            writeError(resp, "Error: surname is empty");
            return true;
        }

        if (contact.getName().isBlank()) {
            writeError(resp, "Error: name is empty");
            return true;
        }

        if (contact.getPhone().isBlank()) {
            writeError(resp, "Error: phone is empty");
            return true;
        }

        return false;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJsonHeaders(resp);

        ContactRepository contactRepository = new ContactInMemoryRepository();

        String searchQuery = req.getParameter("searchText");

        if (searchQuery != null) {
            List<Contact> foundContacts = contactRepository.search(searchQuery);
            mapper.writeValue(resp.getWriter(), foundContacts);
        } else {
            mapper.writeValue(resp.getWriter(), contactRepository.getAll());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJsonHeaders(resp);

        Contact contact = mapper.readValue(req.getReader(), Contact.class);

        if (hasErrors(resp, contact)) {
            return;
        }

        Contact savedContact = new Contact(0, contact.getSurname().trim(),
                contact.getName().trim(), contact.getPhone().trim());

        ContactRepository contactRepository = new ContactInMemoryRepository();
        contactRepository.create(savedContact);

        writeSuccess(resp, HttpServletResponse.SC_CREATED, "Contact created successfully");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJsonHeaders(resp);

        int id = getIdFromPath(req.getPathInfo());

        if (id < 0) {
            writeError(resp, "Error: id is null or NaN");
            return;
        }

        Contact contact = mapper.readValue(req.getReader(), Contact.class);

        if (hasErrors(resp, contact)) {
            return;
        }

        contact.setId(id);

        ContactRepository contactRepository = new ContactInMemoryRepository();
        contactRepository.update(contact);

        writeSuccess(resp, HttpServletResponse.SC_OK, "Contact updated successfully");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJsonHeaders(resp);

        int id = getIdFromPath(req.getPathInfo());

        if (id < 0) {
            writeError(resp, "Error: id is null or NaN");
            return;
        }

        ContactRepository contactRepository = new ContactInMemoryRepository();
        contactRepository.delete(id);

        writeSuccess(resp, HttpServletResponse.SC_NO_CONTENT, "Contact deleted successfully");
    }
}