package com.nexus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.nexus.exception.NexusValidationException;
import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.User;
import com.nexus.service.LogProcessor;
import com.nexus.service.Workspace;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Workspace workspace = new Workspace();
    private static final List<User> users = new ArrayList<>();
    private static final LogProcessor logProcessor = new LogProcessor();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            displayMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "0" -> {
                    System.out.println("Encerrando Nexus Motor...");
                    running = false;
                }
                case "1" -> addUser();
                case "2" -> addProject();
                case "3" -> addTask();
                case "4" -> listTasks();
                case "5" -> {
                    System.out.println("1. Carregar Log V1 (Básico)\n2. Carregar Log V2 (Desafio)");
                    String logChoice = scanner.nextLine();
                    String file = (logChoice.equals("1")) ? "log_v1.txt" : "log_v2.txt";
                    logProcessor.processLog(file, workspace, users);
                }
                default -> System.out.println("\n[!] Opção inválida.");
            }
        }
    }

    private static void displayMenu() {
        System.out.print("""
            
            ======= NEXUS CORE: MENU =======
            1. Adicionar Usuário
            2. Adicionar Projeto
            3. Adicionar Tarefa
            4. Listar Todas as Tarefas
            5. Processar Log de Ações
            0. Sair
            Escolha uma opção:\s""");
    }

    private static void addUser() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();

            User newUser = new User(username, email);
            users.add(newUser);
            System.out.println("[OK] Usuário cadastrado.");
        } catch (NexusValidationException e) {
            System.err.println("[ERRO] " + e.getMessage());
        }
    }

    private static void addProject() {
        System.out.print("Nome do Projeto: ");
        String name = scanner.nextLine();
        System.out.print("Orçamento (horas): ");
        int budget = Integer.parseInt(scanner.nextLine());

        Project project = new Project(name, budget);
        workspace.addProject(project);
        System.out.println("[OK] Projeto criado: " + name);
    }

    private static void addTask() {
        try {
            System.out.print("Título da Tarefa: ");
            String title = scanner.nextLine();
            System.out.print("Prazo (AAAA-MM-DD): ");
            LocalDate deadline = LocalDate.parse(scanner.nextLine());
            System.out.print("Esforço estimado: ");
            int effort = Integer.parseInt(scanner.nextLine());
            System.out.print("Projeto vinculado: ");
            String projectName = scanner.nextLine();

            Task newTask = new Task(title, deadline, effort);
            Project project = workspace.findProjectByName(projectName);
            if (project != null) {
                newTask.assignToProject(project);
            }
            workspace.addTask(newTask);
            System.out.println("[OK] Tarefa adicionada ao backlog.");
        } catch (DateTimeParseException e) {
            System.err.println("[ERRO] Formato de data inválido. Use AAAA-MM-DD.");
        }
    }

    private static void listTasks() {
        List<Task> tasks = workspace.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("\n[!] Nenhuma tarefa no sistema.");
            return;
        }

        String header = "+----+----------------------+-------------+------------+";
        System.out.println("\n" + header);
        System.out.printf("| %-2s | %-20s | %-11s | %-10s |%n", "ID", "TÍTULO", "STATUS", "DEADLINE");
        System.out.println(header);

        for (Task t : tasks) {
            System.out.printf("| %-2d | %-20s | %-11s | %-10s |%n",
                    t.getId(),
                    truncar(t.getTitle(), 20),
                    t.getStatus(),
                    t.getDeadline());
        }
        System.out.println(header);
        System.out.println("Total de tarefas: " + Task.totalTasksCreated);
    }

    private static String truncar(String str, int tam) {
        if (str == null) return "";
        return str.length() > tam ? str.substring(0, tam - 3) + "..." : str;
    }
}