package models;

import java.io.Serializable;

/**
 * Custom generic Singly Linked List implementation from scratch.
 * Replaces Java's built-in ArrayList/LinkedList as required by the specification.
 * Implements Serializable to support saving and loading system data.
 *
 * @param <T> The type of elements stored in this list.
 */
public class MyList<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Inner class representing a single node in the linked list.
     */
    private class Node implements Serializable {
        private static final long serialVersionUID = 1L;
        
        T data;       // The actual object stored (e.g., Campsite, Plot, Booking)
        Node next;    // Reference (pointer) to the next node in the list

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head; // First node in the list
    private int size;  // Current number of elements in the list

    /**
     * Constructor initializes an empty list.
     */
    public MyList() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Adds a new element to the end of the linked list.
     *
     * @param element The element to be added.
     */
    public void add(T element) {
        Node newNode = new Node(element);

        // If the list is empty, the new node becomes the head
        if (head == null) {
            head = newNode;
        } else {
            // Otherwise, traverse to the last node and attach the new node
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    /**
     * Retrieves an element at a specific index (0-based), just like ArrayList.get(i).
     * This makes it easy to use standard for-loops across the application.
     *
     * @param index The position of the element.
     * @return The element at the specified index, or null if out of bounds.
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null; // Index out of bounds
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    /**
     * Removes the first occurrence of the specified element from the list.
     *
     * @param element The element to remove.
     * @return true if the element was found and removed, false otherwise.
     */
    public boolean remove(T element) {
        if (head == null || element == null) {
            return false;
        }

        // Case 1: The element to remove is at the head
        if (head.data.equals(element)) {
            head = head.next; // Move head pointer to the second node
            size--;
            return true;
        }

        // Case 2: The element is somewhere further in the list
        Node current = head;
        while (current.next != null) {
            if (current.next.data.equals(element)) {
                // Bypass the node to remove it from the chain
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }

        return false; // Element not found
    }

    /**
     * Checks if the list contains a specific element (using .equals()).
     * Useful for checking facilities (e.g., if a campsite has "WiFi" or "Pool").
     *
     * @param element The element to search for.
     * @return true if found, false otherwise.
     */
    public boolean contains(T element) {
        Node current = head;
        while (current != null) {
            if (current.data.equals(element)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Clears all elements from the list.
     * Used for the Reset facility.
     */
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Returns the total number of elements in the list.
     *
     * @return current size of the list.
     */
    public int size() {
        return size;
    }

    /**
     * Checks if the list is empty.
     *
     * @return true if the list has no elements, false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }
}