package com.nexus.service;

import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;

public class Workspace {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    public List<String> topPerformers(){
        List<String> toper = tasks.stream()
        .filter(t -> t.getStatus() == TaskStatus.DONE)
        .collect(Collectors.groupingBy((Task t) -> t.getOwner().consultUsername(), Collectors.counting()))
        .entrySet().stream()
        .sorted (Map.Entry.<String, Long>comparingByValue().reversed())
        .map(Map.Entry::getKey)
        .limit(3)
        .collect(Collectors.toList());

        return toper;
    }

    public List<String> overloadedUsers(){
        List<String> overloaded = tasks.stream()
        .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
        .collect(Collectors.groupingBy((Task t) -> t.getOwner().consultUsername(), Collectors.counting()))
        .entrySet().stream()
        .filter(entry -> entry.getValue() > 10)
        .sorted (Map.Entry.<String, Long>comparingByValue().reversed())
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

        return overloaded;
    }
        
    public double projectHealth(){
        long total = tasks.stream()
        .count();

        long done = tasks.stream()
        .filter(t -> t.getStatus() == TaskStatus.DONE)
        .count();

        if(total == 0)
            return 0.0;
        return (done * 100.00) / total;
    }
    
    public TaskStatus globalBottleneck(){
        TaskStatus globalBn = tasks.stream()
        .filter(t -> t.getStatus() != TaskStatus.DONE)
        .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);

        return globalBn;
    }
}