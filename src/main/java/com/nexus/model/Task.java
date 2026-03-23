package com.nexus.model;

import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;

public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private final int id;              // Imutável
    private final LocalDate deadline;  // Imutável
    private final String title;        // Nome da tarefa
    private TaskStatus status;         // Estado atual
    private User owner;                // Usuário responsável
    private final int estimatedEffort; // Esforço em horas

    public Task(String title, LocalDate deadline, int estimatedEffort) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Título da tarefa não pode ser vazio.");
        }
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.estimatedEffort = estimatedEffort;
        this.status = TaskStatus.TO_DO;

        totalTasksCreated++;
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído.
     */
    public void moveToInProgress(User user) {
        if (user == null) {
            totalValidationErrors++;
            throw new NexusValidationException("Não é possível iniciar sem owner.");
        }
        if (this.status == TaskStatus.BLOCKED) {
            totalValidationErrors++;
            throw new NexusValidationException("Não é possível iniciar tarefa bloqueada.");
        }
        this.owner = user;
        this.status = TaskStatus.IN_PROGRESS;
        activeWorkload++;
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() {
        if (this.status == TaskStatus.BLOCKED) {
            totalValidationErrors++;
            throw new NexusValidationException("Não é possível concluir tarefa bloqueada.");
        }
        if (this.status == TaskStatus.IN_PROGRESS) {
            activeWorkload--; // Sai da carga ativa
        }
        this.status = TaskStatus.DONE;
    }

    /**
     * Bloqueia a tarefa.
     * Regra: Pode ser movida para BLOCKED de qualquer estado, exceto DONE.
     */
    public void setBlocked() {
        if (this.status == TaskStatus.DONE) {
            totalValidationErrors++;
            throw new NexusValidationException("Não é possível bloquear tarefa concluída.");
        }
        if (this.status == TaskStatus.IN_PROGRESS) {
            activeWorkload--; // Sai da carga ativa
        }
        this.status = TaskStatus.BLOCKED;
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