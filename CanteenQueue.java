import java.util.Scanner;   


class Order {
    int orderId;            
    String customerName;    
    String item;            
    int quantity;           

   
    Order(int orderId, String customerName, String item, int quantity) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.item = item;
        this.quantity = quantity;
    }

    
    public String toString() {
        return "Order #" + orderId + " | " + customerName + " | " + item + " x" + quantity;
    }
}


class CircularQueue {
    private Order[] data;    
    private int front;       
    private int rear;        
    private int count;       
    private int capacity;    

   
    CircularQueue(int capacity) {
        this.capacity = capacity;
        this.data = new Order[capacity];   
        this.front = 0;                    
        this.rear = -1;                    
        this.count = 0;                    
    }

    
    boolean isEmpty() {
        return count == 0;
    }

  
    boolean isFull() {
        return count == capacity;
    }

    
    int size() {
        return count;
    }

    
    boolean enqueue(Order o) {
        if (isFull()) {
            return false;                  // no free seat, refuse the order
        }
        rear = (rear + 1) % capacity;      // move rear to next seat; % wraps 4 -> 0 (circle!)
        data[rear] = o;                    // put the order in that seat
        count++;                           // one more order waiting
        return true;
    }

    
    Order dequeue() {
        if (isEmpty()) {
            return null;                   // nobody to serve
        }
        Order served = data[front];        // take the front order
        data[front] = null;                // empty that seat
        front = (front + 1) % capacity;    // front moves to the next seat (wraps around)
        count--;                           // one less order waiting
        return served;
    }

  
    Order peek() {
        if (isEmpty()) {
            return null;
        }
        return data[front];
    }

 
    boolean containsId(int id) {
        for (int i = 0; i < count; i++) {
            int seat = (front + i) % capacity;   // walk from front, wrapping around
            if (data[seat].orderId == id) {
                return true;
            }
        }
        return false;
    }

    
    void display() {
        if (isEmpty()) {
            System.out.println("The queue is empty. No pending orders.");
            return;
        }
        System.out.println("Pending orders (front -> rear):");
        for (int i = 0; i < count; i++) {
            int seat = (front + i) % capacity;
            System.out.println("  Seat " + seat + ": " + data[seat]);
        }
    }

    
    void showStatus() {
        System.out.println("Capacity = " + capacity
                + ", Count = " + count
                + ", Front = " + front
                + ", Rear = " + rear
                + ", Empty = " + isEmpty()
                + ", Full = " + isFull());
    }
}


public class CanteenQueue {

    // Asks for a whole number and keeps asking until the user types a valid one
    static int readInt(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String text = sc.nextLine().trim();
            try {
                return Integer.parseInt(text);          // try to turn the text into a number
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please type a whole number.");
            }
        }
    }

    // Asks for text and keeps asking until the user types something non-empty
    static String readText(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String text = sc.nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("Input cannot be blank.");
        }
    }

    // Handles menu option 1: place a new order
    static void placeOrder(Scanner sc, CircularQueue queue) {
        if (queue.isFull()) {                           // check first, so we don't ask for details in vain
            System.out.println("Queue is FULL. Please wait until an order is served.");
            return;
        }
        int id = readInt(sc, "Enter order ID (positive number): ");
        if (id <= 0) {
            System.out.println("Order ID must be positive.");
            return;
        }
        if (queue.containsId(id)) {
            System.out.println("Order ID " + id + " already exists. Use a different ID.");
            return;
        }
        String name = readText(sc, "Enter customer name: ");
        String item = readText(sc, "Enter item name: ");
        int qty = readInt(sc, "Enter quantity (1 or more): ");
        if (qty <= 0) {
            System.out.println("Quantity must be at least 1.");
            return;
        }
        queue.enqueue(new Order(id, name, item, qty));
        System.out.println("Order placed successfully! Orders waiting: " + queue.size());
    }

    // Handles menu option 2: serve the next order
    static void serveOrder(CircularQueue queue) {
        Order served = queue.dequeue();
        if (served == null) {
            System.out.println("Queue is EMPTY. No order to serve.");
        } else {
            System.out.println("Now serving: " + served);
        }
    }

    // Handles menu option 3: view the next order
    static void viewNext(CircularQueue queue) {
        Order next = queue.peek();
        if (next == null) {
            System.out.println("Queue is EMPTY. No order waiting.");
        } else {
            System.out.println("Next order to serve: " + next);
        }
    }

    // The program starts here
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CircularQueue queue = new CircularQueue(5);     // canteen counter has 5 seats

        System.out.println("===== Welcome to the Canteen Order System =====");

        boolean running = true;
        while (running) {                               // repeat the menu until user exits
            System.out.println();
            System.out.println("1. Place order (Enqueue)");
            System.out.println("2. Serve next order (Dequeue)");
            System.out.println("3. View next order (Peek)");
            System.out.println("4. Display all pending orders");
            System.out.println("5. Show queue status (front, rear, count)");
            System.out.println("6. Exit");
            int choice = readInt(sc, "Enter your choice: ");

            switch (choice) {
                case 1: placeOrder(sc, queue); break;
                case 2: serveOrder(queue); break;
                case 3: viewNext(queue); break;
                case 4: queue.display(); break;
                case 5: queue.showStatus(); break;
                case 6: running = false; System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid choice. Please pick 1 to 6.");
            }
        }
        sc.close();
    }
}