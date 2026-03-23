package com.nexus.model;
import java.util.regex.Pattern;
import com.nexus.service.Workspace;

public class User {
    private final String username;
    private final String email;
    private static String EMAIL_REGEX = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static Workspace workspace = new Workspace();

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }

        if (email == null || !EMAIL_PATTERN.matcher(email).matches()){
            throw new IllegalArgumentException("E-mail Inválido");
        }
        this.username = username;
        this.email = email;
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload() {
        int taskcount = 0;
        for(Task task : User.workspace.getTasks()){
            if (task.getOwner().consultUsername() == this.username && task.getStatus() == TaskStatus.IN_PROGRESS){
                taskcount++;
            }
        }
        return taskcount; 
    }
}