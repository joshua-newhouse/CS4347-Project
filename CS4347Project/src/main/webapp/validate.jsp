<%--
  Created by IntelliJ IDEA.
  User: Miguel
  Date: 27-Nov-17
  Time: 09:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import = "java.sql.*" %>
<%
    try{
        String username = request.getParameter("Username");
        String password = request.getParameter("Password");
        Class.forName("com.mysql.jdbc.Driver");  // MySQL database connection
        Connection conn = DriverManager.getConnection("jdbc:mysql://cs4347-project.cujq9m2vjohw.us-east-1.rds.amazonaws.com:3306" + "user=mdelarocha&password=CrOliNAr");
        PreparedStatement pst = conn.prepareStatement("Select username,password from finance.Users where username=? and password=?");
        pst.setString(1, username);
        pst.setString(2, password);
        ResultSet rs = pst.executeQuery();
        if(rs.next())
            out.println("Valid login credentials");
        else
            out.println("Invalid login credentials");
    }
    catch(Exception e){
        out.println("Something went wrong !! Please try again");
    }
%>
