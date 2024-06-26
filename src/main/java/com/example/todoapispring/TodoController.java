package com.example.todoapispring;

import com.example.todoapispring.Model.ResponseModel;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/todos")
public class TodoController {

    private TodoService todoService; // anotherTodoService

    private TodoService todoservice2; // fakeTodoService

    private static List<Todo> todoList;
    // Error message when the todo is not found
    private static final String TODO_NOT_FOUND = "Todo not found";

    public TodoController(
            @Qualifier("anotherTodoService") TodoService todoService,
            @Qualifier("fakeTodoService")  TodoService todoservice2) {

        this.todoService = todoService;
        this.todoservice2 = todoservice2;
        todoList = new ArrayList<>();
        todoList.add(new Todo(1, false, "Todo 1", 1));
        todoList.add(new Todo(2, true, "Todo 2", 2));
    }


    @GetMapping
    public ResponseEntity<List<Todo>> getTodos(@RequestParam(required = false) Boolean isCompleted) {
        System.out.println("Incoming query params: " + isCompleted + " " + this.todoService.doSomething());
        return ResponseEntity.ok(todoList);
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo newTodo) {

        /**
         * we can use this annotation to set the status code @ResponseStatus(HttpStatus.CREATED)
         *
         */
        todoList.add(newTodo);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTodo);
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<ResponseModel> getTodoById(@PathVariable Long todoId) {
        for (Todo todo : todoList) {
            if (todo.getId() == todoId) {
                return ResponseEntity.ok(new ResponseModel(true, todo));
            }
        }
        // HW: Along with 404 status code, try to send a json {message: Todo not found}
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel(false,"todo not found"));
    }

    @PatchMapping("/{todoId}")
    public ResponseEntity<ResponseModel> patchTodo(@PathVariable int todoId,@RequestBody Todo todo)
    {
        Todo getTodo = todoList.stream().filter(x-> x.getId() == todoId).findFirst().orElse(null);
        if(getTodo == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel(false,"todo not found"));
        else
        {
            getTodo.setCompleted(todo.isCompleted());
            getTodo.setTitle(todo.getTitle());
            getTodo.setUserId(todo.getUserId());
            return ResponseEntity.ok(new ResponseModel(false,todo));
        }
    }
    @DeleteMapping("/{todoId}")
    public ResponseEntity<ResponseModel> deleteTodoById(@PathVariable int todoId) {
        for (Todo todo : todoList) {
            if (todo.getId() == todoId) {
                todoList.remove(todo);
                return ResponseEntity.ok(new ResponseModel(true, todo));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel(false,"todo not found"));
    }
}
