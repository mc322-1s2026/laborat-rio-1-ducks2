package com.nexus.service;

import com.nexus.model.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class LogProcessor {

    public void processLog(String fileName, Workspace workspace, List<User> users) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";");

                try {
                    switch (parts[0]) {
                        case "CREATE_USER" -> {
                            if (parts.length < 3) {
                                System.err.println("[ERRO DE REGRAS] Comando inválido: '" + line +
                                        "'. Esperado formato CREATE_USER;username;email");
                                Task.totalValidationErrors++;
                                continue;
                            }
                            try {
                                User user = new User(parts[1], parts[2]);
                                users.add(user);
                                System.out.println("[LOG] Usuário criado: " + parts[1]);
                            } catch (Exception e) {
                                Task.totalValidationErrors++;
                                System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                            }
                        }

                        case "CREATE_PROJECT" -> {
                            if (parts.length < 3) {
                                System.err.println("[ERRO DE REGRAS] Comando inválido: '" + line +
                                        "'. Esperado formato CREATE_PROJECT;name;budget");
                                Task.totalValidationErrors++;
                                continue;
                            }
                            try {
                                Project project = new Project(parts[1], Integer.parseInt(parts[2]));
                                workspace.addProject(project);
                                System.out.println("[LOG] Projeto criado: " + parts[1]);
                            } catch (Exception e) {
                                Task.totalValidationErrors++;
                                System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                            }
                        }

                        case "CREATE_TASK" -> {
                            if (parts.length < 5) {
                                System.err.println("[ERRO DE REGRAS] Comando inválido: '" + line +
                                        "'. Esperado formato CREATE_TASK;title;deadline;effort;projectName");
                                Task.totalValidationErrors++;
                                continue;
                            }
                            try {
                                Task task = new Task(parts[1],
                                        java.time.LocalDate.parse(parts[2]),
                                        Integer.parseInt(parts[3]));
                                Project project = workspace.findProjectByName(parts[4]);
                                if (project == null) {
                                    Task.totalValidationErrors++;
                                    System.err.println("[ERRO DE REGRAS] Falha no comando '" + line +
                                            "': Projeto não encontrado.");
                                    continue;
                                }
                                task.assignToProject(project);
                                workspace.addTask(task);
                                System.out.println("[LOG] Tarefa criada: " + parts[1] + " no projeto " + parts[4]);
                            } catch (Exception e) {
                                Task.totalValidationErrors++;
                                System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                            }
                        }

                        case "ASSIGN_USER" -> {
                            if (parts.length < 3) {
                                System.err.println("[ERRO DE REGRAS] Comando inválido: '" + line +
                                        "'. Esperado formato ASSIGN_USER;taskId;username");
                                Task.totalValidationErrors++;
                                continue;
                            }
                            int taskId = Integer.parseInt(parts[1]);
                            String username = parts[2];

                            Task task = workspace.findTaskById(taskId);
                            User user = users.stream()
                                    .filter(u -> u.getUsername().equals(username))
                                    .findFirst()
                                    .orElse(null);

                            if (task == null) {
                                Task.totalValidationErrors++;
                                System.err.println("[ERRO DE REGRAS] Falha no comando '" + line +
                                        "': Tarefa " + taskId + " não existe.");
                                continue;
                            }
                            if (user == null) {
                                Task.totalValidationErrors++;
                                System.err.println("[ERRO DE REGRAS] Falha no comando '" + line +
                                        "': Usuário '" + username + "' não encontrado.");
                                continue;
                            }

                            task.setOwner(user);
                            System.out.println("[LOG] Usuário " + username + " atribuído à tarefa " + taskId);
                        }

                        case "CHANGE_STATUS" -> {
                            if (parts.length < 3) {
                                System.err.println("[ERRO DE REGRAS] Comando inválido: '" + line +
                                        "'. Esperado formato CHANGE_STATUS;taskId;newStatus");
                                Task.totalValidationErrors++;
                                continue;
                            }
                            int taskId = Integer.parseInt(parts[1]);
                            String statusStr = parts[2];

                            Task task = workspace.findTaskById(taskId);
                            if (task == null) {
                                Task.totalValidationErrors++;
                                System.err.println("[ERRO DE REGRAS] Falha no comando '" + line +
                                        "': Tarefa " + taskId + " não existe.");
                                continue;
                            }

                            try {
                                TaskStatus newStatus = TaskStatus.valueOf(statusStr);
                                task.changeStatus(newStatus);
                                System.out.println("[LOG] Status da tarefa " + taskId + " alterado para " + newStatus);
                            } catch (Exception e) {
                                Task.totalValidationErrors++;
                                System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                            }
                        }

                        case "REPORT_STATUS" -> {
                            workspace.printReports(users);
                        }

                        default -> {
                            System.err.println("[ERRO DE REGRAS] Comando desconhecido: " + line);
                            Task.totalValidationErrors++;
                        }
                    }
                } catch (Exception e) {
                    Task.totalValidationErrors++;
                    System.err.println("[ERRO DE REGRAS] Falha inesperada no comando '" + line + "': " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[ERRO FATAL] Arquivo não encontrado no classpath: " + fileName);
        }
    }
}