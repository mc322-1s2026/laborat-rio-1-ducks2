package com.nexus.model;
<<<<<<< HEAD
=======
import java.util.regex.Pattern;
import com.nexus.service.Workspace;
>>>>>>> faf9d190b214b9c872d49070db46c8c794296feb

public class User {
    private final String username;
    private final String email;
<<<<<<< HEAD
=======
    private static String EMAIL_REGEX = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static Workspace workspace = new Workspace();
>>>>>>> faf9d190b214b9c872d49070db46c8c794296feb

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
<<<<<<< HEAD
=======
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()){
            throw new IllegalArgumentException("E-mail Inválido");
        }
>>>>>>> faf9d190b214b9c872d49070db46c8c794296feb
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
<<<<<<< HEAD
        return 0; 
=======
        int taskcount = 0;
        for(Task task : User.workspace.getTasks()){
            if (task.getOwner().consultUsername() == this.username && task.getStatus() == TaskStatus.IN_PROGRESS){
                taskcount++;
            }
        }
        return taskcount; 
>>>>>>> faf9d190b214b9c872d49070db46c8c794296feb
    }
}