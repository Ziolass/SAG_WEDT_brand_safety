package com.sag_wedt.brand_safety.observerActor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObserverServlet extends HttpServlet {

    private List<Statistics> stats = new ArrayList<Statistics>();

    public ObserverServlet() {
        stats.add(new Statistics(0,0,0));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("statistics",
                getCustomers(resultSize(request.getParameter("size"))));
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private int resultSize(String sizeParam) {
        return sizeParam==null?stats.size():Integer.parseInt(sizeParam);
    }

    private List<Statistics> getCustomers(int size) {
        return stats.subList(Math.max(stats.size()-size,0), stats.size()-1);
    }

    public void addStatistics(int time, long textClassifier, long frontend) {
        stats.add(new Statistics(stats.size(), stats.get(stats.size()-1).getFrontendActors() + textClassifier,
                stats.get(stats.size()-1).getTextClassifierActors() + frontend));
    }

}
