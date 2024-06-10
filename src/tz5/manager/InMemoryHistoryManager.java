package tz5.manager;
import tz5.model.Node;
import tz5.model.Task;

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
            tail.setNext(newHead);
        }
    }

    private List<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Node<Task> taskNode : historyMap.values()) {
            tasks.add(taskNode.getData());
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> next = node.getNext();
            final Node<Task> prev = node.getPrev();
            node.setData(null);
            if (firstNode == node && lastNode == node) {
                firstNode = null;
                lastNode = null;
            } else if (firstNode == node) {
                firstNode = firstNode.getNext();
                firstNode.setPrev(null);
            } else if (lastNode == node) {
                lastNode = lastNode.getPrev();
                lastNode.setNext(null);
            } else {
                prev.setNext(next);
                next.setPrev(prev);
            }
        }
    }
}
