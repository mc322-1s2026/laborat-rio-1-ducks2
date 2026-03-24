package com.nexus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nexus.exception.NexusValidationException;

/**
 * Representa um projeto dentro do Nexus.
 * Um projeto agrupa tarefas e possui um orçamento total em horas.
 */
public class Project {
    private final String name;
    private final int totalBudget;
    private final List<Task> tasks;

    public Project(String name, int totalBudget) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome do projeto não pode ser vazio.");
        }
        if (totalBudget <= 0) {
            throw new IllegalArgumentException("Orçamento deve ser maior que zero.");
        }
        this.name = name;
        this.totalBudget = totalBudget;
        this.tasks = new ArrayList<>();
    }

    /**
     * Adiciona uma tarefa ao projeto, validando se o esforço total não excede o orçamento.
     */
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Tarefa não pode ser nula.");
        }

        int effortSum = tasks.stream()
                .mapToInt(Task::getEstimatedEffort)
                .sum() + task.getEstimatedEffort();

        if (effortSum > totalBudget) {
            throw new NexusValidationException("Orçamento do projeto excedido ao adicionar tarefa.");
        }

        tasks.add(task);
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public String getName() {
        return name;
    }

    public int getTotalBudget() {
        return totalBudget;
    }

    /**
     * Calcula o percentual de conclusão do projeto.
     */
    public double getCompletionPercentage() {
        if (tasks.isEmpty()) return 0.0;

        long doneCount = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .count();

        return (doneCount * 100.0) / tasks.size();
    }
}