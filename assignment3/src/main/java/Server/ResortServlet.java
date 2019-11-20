package Server;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ResortServlet", value = "/resorts")
public class ResortServlet extends HttpServlet {

  protected void doPost(HttpServletRequest req,
      HttpServletResponse res)
      throws javax.servlet.ServletException, IOException {
    PrintWriter out = res.getWriter();
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    Map<String, String[]> body = req.getParameterMap();
//    System.out.println(urlPath);

    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("missing paramterers");
      out.write(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (!isUrlValid(urlParts) && isBodyValid(body)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("invalid url");
      out.write(jsonString);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url param
      String jsonString = new Gson().toJson("It works!");
      out.write(jsonString);
    }
  }

  protected void doGet(HttpServletRequest req,
      HttpServletResponse res)
      throws javax.servlet.ServletException, IOException {
    PrintWriter out = res.getWriter();
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
//    System.out.println(urlPath);

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("missing paramterers");
      out.write(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("invalid url");
      out.write(jsonString);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url param
      String jsonString = new Gson().toJson("It works!");
      out.write(jsonString);
    }
  }

  private boolean isUrlValid(String[] urlPath) {
    if (urlPath.length == 0)
      return true;
    else if (urlPath.length == 3 && NumericHelper.isInteger(urlPath[1])
        && urlPath[2].equals("seasons")) {
      return true;
    }
    return false;
  }

  private boolean isBodyValid(Map<String, String[]> body) {
    if (body.containsKey("year") && body.size() == 1) {
      return true;
    }
    return false;
  }
}