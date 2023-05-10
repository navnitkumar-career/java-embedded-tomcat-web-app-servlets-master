package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mjson.Json;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(name = "JsonServlet", urlPatterns = {"/json"})
public class JsonServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (ServletOutputStream out = resp.getOutputStream()) {

            String json = Json.object().set("time", LocalDateTime.now()).toString();

            resp.setHeader("Content-Type", "application/json");
            out.write(json.getBytes());
        }
    }
}
