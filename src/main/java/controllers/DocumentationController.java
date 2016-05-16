package controllers;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

import javax.ws.rs.core.Context;
import javax.ws.rs.GET;
import java.io.IOException;


    @Path("/documentation")
    public class DocumentationController extends HttpServlet{

        @GET
        public void getHome(@Context HttpServletRequest request,
                            @Context HttpServletResponse response) throws ServletException, IOException {
            request.getRequestDispatcher("/WEB-INF/views/documentation.jsp")
                    .forward(request, response);
        }

    }
