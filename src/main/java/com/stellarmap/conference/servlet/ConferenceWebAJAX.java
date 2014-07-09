package com.stellarmap.conference.servlet;

import com.stellarmap.conference.rest.CanonicalFormAssociationRestCmd;
import com.stellarmap.conference.rest.CanonicalFormRestCmd;
import com.stellarmap.conference.rest.ConferenceRestCmd;
import com.stellarmap.conference.rest.IRestCmd;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Servlet for handing AJAX style requests relating to conferences.
 * Created by barclakj on 07/07/2014.
 */
@WebServlet(name="conferencewebrest", urlPatterns = {"/rest/*"})
public class ConferenceWebAJAX extends HttpServlet {
    private static Logger log = Logger.getLogger(ConferenceWebAJAX.class.getCanonicalName());

    private static Map<String, IRestCmd> commandMap = new HashMap<String, IRestCmd>();

    static {
        commandMap.put("canonical", new CanonicalFormRestCmd());
        commandMap.put("associations", new CanonicalFormAssociationRestCmd());
        commandMap.put("conference", new ConferenceRestCmd());
    }

    class RestRequest {
        public String method = null;
        public String type = null;
        public String item = null;
    }

    /**
     * Convert an http request to a usable restrequest.
     * @param req
     * @param method
     * @return
     */
    private RestRequest toRestRequest(HttpServletRequest req, String method) {
        String path = req.getRequestURI();

        String ctx = req.getContextPath() + "/rest/";

        String reqPath = path.substring(ctx.length());
        String type = null;
        String item = null;

        if (reqPath.indexOf("/")>=0) {
            // request is for a specific item.
            type = reqPath.substring(0, reqPath.indexOf("/"));
            item = reqPath.substring(reqPath.indexOf("/")+1);
            if (item!=null && item.trim().length()==0) {
                item = null;
            }
        } else {
            // request is for *all* items
            type = reqPath;
        }

        RestRequest rest = new RestRequest();
        rest.item = item;
        rest.type = type;
        rest.method = method;
        return rest;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject jsonObject = null;
        RestRequest restRequest = this.toRestRequest(req, "GET");

        IRestCmd cmd = commandMap.get(restRequest.type);
        if (cmd!=null) {
            jsonObject = cmd.doGet(restRequest.type, restRequest.item);

            if (jsonObject!=null) {
                resp.getWriter().write(jsonObject.toString());
            } else {
                log.warning("No response received");
                resp.sendError(500);
            }
        } else {
            log.warning("Unknown command: " + restRequest.type);
            resp.sendError(404);
        }
   }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject jsonObject = null;
        RestRequest restRequest = this.toRestRequest(req, "POST");

        IRestCmd cmd = commandMap.get(restRequest.type);
        if (cmd!=null) {
            jsonObject = cmd.doPost(restRequest.type, restRequest.item);

            if (jsonObject!=null) {
                resp.getWriter().write(jsonObject.toString());
            } else {
                log.warning("No response received");
                resp.sendError(500);
            }
        } else {
            log.warning("Unknown command: " + restRequest.type);
            resp.sendError(404);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject jsonObject = null;
        RestRequest restRequest = this.toRestRequest(req, "PUT");

        IRestCmd cmd = commandMap.get(restRequest.type);
        if (cmd!=null) {
            jsonObject = cmd.doPut(restRequest.type, restRequest.item);

            if (jsonObject!=null) {
                resp.getWriter().write(jsonObject.toString());
            } else {
                log.warning("No response received");
                resp.sendError(500);
            }
        } else {
            log.warning("Unknown command: " + restRequest.type);
            resp.sendError(404);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject jsonObject = null;
        RestRequest restRequest = this.toRestRequest(req, "DELETE");

        IRestCmd cmd = commandMap.get(restRequest.type);
        if (cmd!=null) {
            jsonObject = cmd.doDelete(restRequest.type, restRequest.item);

            if (jsonObject!=null) {
                resp.getWriter().write(jsonObject.toString());
            } else {
                log.warning("No response received");
                resp.sendError(500);
            }
        } else {
            log.warning("Unknown command: " + restRequest.type);
            resp.sendError(404);
        }
    }


}
