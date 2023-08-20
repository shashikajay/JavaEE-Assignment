package lk.ijse.jsp.servlet;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/pages/item")
public class ItemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/webpos?allowPublicKeyRetrieval=true&useSSL=false", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("select * from iteminfo");
            ResultSet rst = pstm.executeQuery();
            resp.addHeader("Content-type","application/json");

            /*String json="[";
            while (rst.next()) {
                String items="{";
                String code = rst.getString(1);
                String name = rst.getString(2);
                String qtyOnHand = rst.getString(3);
                String unitPrice = rst.getString(4);

                items+="\"id\":\""+code+"\",";
                items+="\"name\":\""+name+"\",";
                items+="\"qtyOnHand\":\""+qtyOnHand+"\",";
                items+="\"unitPrice\":\""+unitPrice+"\"";
                items+="},";
                json+=items;
            }
            json=json+"]";

            resp.getWriter().print(json.substring(0,json.length()-2)+"]");*/

            JsonArrayBuilder allItems = Json.createArrayBuilder();
            while (rst.next()) {
                String code = rst.getString(1);
                String name = rst.getString(2);
                String qtyOnHand = rst.getString(3);
                String unitPrice = rst.getString(4);

                JsonObjectBuilder objectBuilder= Json.createObjectBuilder();
                objectBuilder.add("code",code);
                objectBuilder.add("name",name);
                objectBuilder.add("qtyOnHand",qtyOnHand);
                objectBuilder.add("unitPrice",unitPrice);

                allItems.add(objectBuilder.build());
            }
            resp.getWriter().print(allItems.build());

        } catch (SQLException e) {

            resp.setStatus(400);
            resp.getWriter().print(addJSONObject(e.getMessage(), "error"));

        } catch (ClassNotFoundException e) {

            resp.setStatus(500);
            resp.getWriter().print(addJSONObject(e.getMessage(), "error"));

        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Content-type", "application/json");
        String code = req.getParameter("code");
        String itemName = req.getParameter("description");
        String qty = req.getParameter("qty");
        String unitPrice = req.getParameter("unitPrice");
        String option = req.getParameter("option");
//
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "sanu1234");

            PreparedStatement pstm = connection.prepareStatement("insert into Item values(?,?,?,?)");
            pstm.setObject(1, code);
            pstm.setObject(2, itemName);
            pstm.setObject(3, qty);
            pstm.setObject(4, unitPrice);


            if (pstm.executeUpdate() > 0) {
                resp.getWriter().print(addJSONObject("Customer Saved !", "ok"));
            }
        } catch (SQLException e) {

            resp.setStatus(400);
            resp.getWriter().print(addJSONObject(e.getMessage(), "error"));

        } catch (ClassNotFoundException e) {

            resp.setStatus(500);
            resp.getWriter().print(addJSONObject(e.getMessage(), "error"));

        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Content-type", "application/json");
        String code = req.getParameter("itemID");
        System.out.println("delete"+code);

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "sanu1234");
            PreparedStatement pstm2 = connection.prepareStatement("delete from Item where code=?");
            pstm2.setObject(1, code);
            if (pstm2.executeUpdate() > 0) {
                resp.getWriter().print(addJSONObject("Customer Saved !", "ok"));
            }
        } catch (SQLException e) {

            resp.setStatus(400);
            resp.getWriter().print(addJSONObject(e.getMessage(), "error"));

        } catch (ClassNotFoundException e) {

            resp.setStatus(500);
            resp.getWriter().print(addJSONObject(e.getMessage(), "error"));

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Content-type", "application/json");
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject customerOB = reader.readObject();

        String code = customerOB.getString("code");
        String desc = customerOB.getString("name");
        String qty = customerOB.getString("qty");
        String price = customerOB.getString("price");

        System.out.println("Update"+desc);

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "sanu1234");
            PreparedStatement pstm3 = connection.prepareStatement("update Item set description=?,qtyOnHand=?,unitPrice=? where code=?");

            pstm3.setObject(1, desc);
            pstm3.setObject(2, qty);
            pstm3.setObject(3, price);
            pstm3.setObject(4, code);
            
            if (pstm3.executeUpdate() > 0) {
                resp.getWriter().print(addJSONObject("Customer Saved !", "ok"));
            }
        } catch (SQLException e) {

            resp.setStatus(400);
            resp.getWriter().print(addJSONObject(e.getMessage(), "error"));

        } catch (ClassNotFoundException e) {

            resp.setStatus(500);
            resp.getWriter().print(addJSONObject(e.getMessage(), "error"));

        }

    }

    private JsonArrayBuilder addJSONObject(String message, String state) {
        JsonArrayBuilder status = Json.createArrayBuilder();

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("state", state);
        objectBuilder.add("message", message);
        objectBuilder.add("data", "[]");
        status.add(objectBuilder.build());

        return status;
    }
}
