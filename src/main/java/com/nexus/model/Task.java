package com.nexus.model;
import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;
import java.time.LocalDate; 
import com.nexus.exception.NexusValidationException;

/**
 * Representa uma tarefa no sistema Nexus.
 * Implementa regras de máquina de estados, telemetria global e integração com Project.
 */
public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private final int id;              
    private final LocalDate deadline;  
    private final String title;        
    private TaskStatus status;         
    private User owner;                
    private final int estimatedEffort; 
    private Project project;           // Novo: vínculo com Project

    public Task(String title, LocalDate deadline, int estimatedEffort) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Título da tarefa não pode ser vazio.");
        }
        if (deadline == null) {
            throw new IllegalArgumentException("Deadline não pode ser nulo.");
        }
        if (estimatedEffort <= 0) {
            throw new IllegalArgumentException("Esforço estimado deve ser maior que zero.");
        }

        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.estimatedEffort = estimatedEffort;
        this.status = TaskStatus.TO_DO;

        totalTasksCreated++;
    }

    /**
     * Associa esta tarefa a um projeto.
     * A validação de orçamento é feita pelo próprio Project.
     */
    public void assignToProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Projeto não pode ser nulo.");
        }
        project.addTask(this);
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    // Regras de transição de estados (já implementadas antes)
    public void changeStatus(TaskStatus newStatus) {
        switch (newStatus) {
            case IN_PROGRESS -> {
                if (owner == null) {
                    totalValidationErrors++;
                    throw new NexusValidationException("Não é possível iniciar sem owner atribuído.");
                }
                if (status == TaskStatus.BLOCKED) {
                    totalValidationErrors++;
                    throw new NexusValidationException("Não é possível iniciar tarefa bloqueada.");
                }
                this.status = TaskStatus.IN_PROGRESS;
                activeWorkload++;
            }
            case DONE -> {
                if (status == TaskStatus.BLOCKED) {
                    totalValidationErrors++;
                    throw new NexusValidationException("Não é possível concluir tarefa bloqueada.");
                }
                if (status == TaskStatus.IN_PROGRESS) {
                    activeWorkload--;
                }
                this.status = TaskStatus.DONE;
            }
            case BLOCKED -> {
                if (status == TaskStatus.DONE) {
                    totalValidationErrors++;
                    throw new NexusValidationException("Não é possível bloquear tarefa concluída.");
                }
                if (status == TaskStatus.IN_PROGRESS) {
                    activeWorkload--;
                }
                this.status = TaskStatus.BLOCKED;
            }
            case TO_DO -> {
                totalValidationErrors++;
                throw new NexusValidationException("Não é permitido retornar ao estado TO_DO.");
            }
        }
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    // Getters
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }
    public int getEstimatedEffort() { return estimatedEffort; }
}