/* Implement this class. */

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

class PriorityComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if (o1.getPriority() < o2.getPriority())
            return 1;
        else if (o1.getPriority() > o2.getPriority())
            return -1;
        else if (o1.getPriority() == o2.getPriority()) {
            if (o1.getStart() > o2.getStart())
                return 1;
            else if (o1.getStart() < o2.getStart())
                return -1;
        }
        return 0;
    }
}


public class MyHost extends Host {
    public AtomicBoolean running = new AtomicBoolean(false);
    public AtomicBoolean done = new AtomicBoolean(false);
    public AtomicLong sum = new AtomicLong(0);
    Comparator<Task> comparator = new PriorityComparator();

    PriorityQueue<Task> host_queue = new PriorityQueue<>(new PriorityComparator());
    @Override
    public void run() {

            // cat timp done este false, run nu se opreste
            while (done.get() == false) {
                // facem busy waiting, verificam repetat daca avem elemente in coada
                if (host_queue.isEmpty() == false && running.get() == false) {

                    // preia task-ul din varf
                    Task task = host_queue.poll();
                    running.set(true);

                    // rulam task-ul prin sleep
                    while (task.getLeft() != 0) {

                        // verificam daca este preemptibil si daca task-ul din coada este prioritar
                        if (host_queue.isEmpty() == false) {
                            if (task.isPreemptible() && comparator.compare(task, host_queue.peek()) == 1) {
                                    host_queue.add(task);
                                    task = host_queue.poll();
                            }
                        }

                        // consumam timp
                        try {
                            Thread.sleep(200L);
                        } catch (InterruptedException e) {
                            System.out.println(e);
                        }

                        // scadem timpul consumat din left si din sum
                        task.setLeft(task.getLeft() - 200L);
                        sum.set(sum.get() - 200L);
                    }

                    // terminam finish-ul
                    task.finish();
                    running.set(false);
                }
            }
    }

    @Override
    public void addTask(Task task) {
        // adaugam in coada si in sum timpul
        host_queue.add(task);
        sum.getAndAdd(task.getLeft());
    }

    @Override
    public int getQueueSize() {
        // for sq
        if (running.get()) {
            // aduni 1 la numarul curent de queue
            return host_queue.size() + 1;
        }
        return host_queue.size();
    }

    @Override
    public long getWorkLeft() {
        return sum.get();
    }

    @Override
    public void shutdown() {
        // setam done true ca run sa se opreasca din ciclul de rulare
        done.set(true);
    }
}
