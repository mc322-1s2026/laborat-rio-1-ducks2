package com.nexus.service;

import com.nexus.model.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import com.nexus.model.TaskStatus;
import com.nexus.exception.NexusValidationException;

public class LogProcessor {

    public void processLog(String fileName, Workspace workspace, List<User> users) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(";");

                try {
                    switch (p[0]) {
                        case "CREATE_USER" -> {
                            if (p.length < 3) {
                                System.err.println("[ERRO DE REGRAS] Comando inválido: '" + line +
                                        "'. Esperado formato CREATE_USER;username;email");
                                Task.totalValidationErrors++;
                                continue;
                            }
                            try {
                                User user = new User(p[1], p[2]);
                                users.add(user);
                                System.out.println("[LOG] Usuário criado: " + p[1]);
                            } catch (Exception e) {
                                Task.totalValidationErrors++;
                                System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                            }
                        }
                        case "ASSIGN_USER" -> {
                            if (p.length < 3) {
                                System.err.println("[ERRO DE REGRAS] Comando inválido: '" + line +
                                        "'. Esperado formato ASSIGN_USER;taskId;username");
                                Task.totalValidationErrors++;
                                continue;
                            }
                            int taskId = Integer.parseInt(p[1]);
                            String username = p[2];

                            Task task = workspace.findTaskById(taskId);
                            User user = users.stream()
                                    .filter(u -> u.consultUsername().equals(username))
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
                        case "REPORT_STATUS" -> {
                                List<String> topPerformers = workspace.topPerformers();
                                System.out.println("[REPORT STATUS] Top performers: " + topPerformers);
                                List<String> overloadedUsers = workspace.overloadedUsers();
                                System.out.println("[REPORT STATUS] Usuários sobrecarregados: " + overloadedUsers);
                                double projectHealth = workspace.projectHealth();
                                System.out.println("[REPORT STATUS] Percentual de Conclusão: " + String.format("%.2f", projectHealth) + "%");
                                TaskStatus globalBottleneck = workspace.globalBottleneck();
                                System.out.println("[REPORT STATUS] Status com mais tarefas: " + globalBottleneck);
                            }

                        case "CREATE_PROJECT" -> {
                            if (p.length < 3) {
                                System.err.println("[ERRO DE REGRAS] Comando inválido: '" + line +
                                        "'. Esperado formato CREATE_PROJECT;name;budget");
                                Task.totalValidationErrors++;
                                continue;
                            }
                            try {
                                Project project = new Project(p[1], Integer.parseInt(p[2]));
                                workspace.addProject(project);
                                System.out.println("[LOG] Projeto criado: " + p[1]);
                            } catch (Exception e) {
                                Task.totalValidationErrors++;
                                System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                            }
                        }

                        case "CREATE_TASK" -> {
                            if (p.length < 5) {
                                System.err.println("[ERRO DE REGRAS] Comando inválido: '" + line +
                                        "'. Esperado formato CREATE_TASK;title;deadline;effort;projectName");
                                Task.totalValidationErrors++;
                                continue;
                            }
                            try {
                                Task task = new Task(p[1],
                                        java.time.LocalDate.parse(p[2]),
                                        Integer.parseInt(p[3]));
                                Project project = workspace.findProjectByName(p[4]);
                                if (project == null) {
                                    Task.totalValidationErrors++;
                                    System.err.println("[ERRO DE REGRAS] Falha no comando '" + line +
                                            "': Projeto não encontrado.");
                                    continue;
                                }
                                task.assignToProject(project);
                                workspace.addTask(task);
                                System.out.println("[LOG] Tarefa criada: " + p[1] + " no projeto " + p[4]);
                            } catch (Exception e) {
                                Task.totalValidationErrors++;
                                System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                            }
                        }

                        case "CHANGE_STATUS" -> {
                            if (p.length < 3) {
                                System.err.println("[ERRO DE REGRAS] Comando inválido: '" + line +
                                        "'. Esperado formato CHANGE_STATUS;taskId;newStatus");
                                Task.totalValidationErrors++;
                                continue;
                            }
                            int taskId = Integer.parseInt(p[1]);
                            String statusStr = p[2];

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