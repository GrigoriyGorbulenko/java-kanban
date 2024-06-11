package tz.manager;
import tz.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> historyMap = new LinkedHashMap<>();
    private Node<Task> firstNode = null;
    private Node<Task> lastNode = null;

    @Override
    public void addToTask(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node<Task> tail = lastNode;
        final Node<Task> newHead = new Node<>(task, null, tail);
        lastNode = newHead;
        historyMap.put(task.getId(), newHead);
        if (tail == null) {
            firstNode = newHead;
        } else {
            tail.next = newHead;
        }
    }

    private List<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Node<Task> taskNode : historyMap.values()) {
            tasks.add(taskNode.data);
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;
            node.data = null;
            if (firstNode == node && lastNode == node) {
                firstNode = null;
                lastNode = null;
            } else if (firstNode == node) {
                firstNode = firstNode.next;
                firstNode.prev = null;
            } else if (lastNode == node) {
                lastNode = lastNode.prev;
                lastNode.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }
    }


    private class Node<T> {

        private T data;
        private Node<T> next;
        private Node<T> prev;

        public Node(T data, Node<T> next, Node<T> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}
