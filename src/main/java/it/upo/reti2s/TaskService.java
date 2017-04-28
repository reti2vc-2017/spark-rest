package it.upo.reti2s;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

/**
 * The main REST service: it starts the server and handles all the HTTP requests.
 * @author <a href="mailto:luigi.derussis@uniupo.it">Luigi De Russis</a>
 * @version 1.0 (28/04/2017)
 */
public class TaskService {

    public static void main(String[] args) {

        TaskDao taskDao = new TaskDao();
        Gson gson = new Gson();
        String baseURL = "/api/v1.0";

        // get all tasks
        get(baseURL + "/tasks", "application/json", (request, response) -> {
            response.status(200);
            response.type("application/json");
            List<Task> allTasks = taskDao.getAllTasks();
            Map<String, List<Task>> finalJson = new HashMap<>();
            finalJson.put("tasks", allTasks);
            return finalJson;
        }, gson::toJson);

        // get the task with the specified id
        get(baseURL + "/tasks/:id", "application/json", (request, response) -> {
            Task task = taskDao.getTask(Integer.valueOf(request.params(":id")));

            if(task == null)
                halt(404);

            Map<String, Task> finalJson = new HashMap<>();
            finalJson.put("task", task);
            response.status(200);
            response.type("application/json");
            return finalJson;
        }, gson::toJson);

        // add a new task
        post(baseURL + "/tasks", (request, response) -> {
            Map addRequest = gson.fromJson(request.body(), Map.class);

            if(addRequest!=null && addRequest.containsKey("description") && addRequest.containsKey("urgent"))
            {
                String description = String.valueOf(addRequest.get("description"));
                // gson convert JSON num in double, but we need an int
                int urgent = ((Double) addRequest.get("urgent")).intValue();
                taskDao.addTask(description, urgent);
                response.status(201);
            }
            else {
                halt(403);
            }
             return "";
        });

    }

}
