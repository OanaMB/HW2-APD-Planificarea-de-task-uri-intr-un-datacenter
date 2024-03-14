/* Implement this class. */

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDispatcher extends Dispatcher {
    private AtomicInteger robin_number = new AtomicInteger(0);
    private AtomicInteger hosts_size = new AtomicInteger(this.hosts.size());
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task) {
        // IMPLEMENTARE ROUND ROBIN
        synchronized(this) {
            if (this.algorithm == SchedulingAlgorithm.ROUND_ROBIN) {

                    this.hosts.get(robin_number.getAndIncrement()).addTask(task);
                    robin_number.set(robin_number.get() % hosts_size.get());

            }

            // IMPLEMENTARE SITA
            if (this.algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
                synchronized (this) {
                    switch (task.getType()) {
                        case SHORT:
                            this.hosts.get(0).addTask(task);
                            break;
                        case MEDIUM:
                            this.hosts.get(1).addTask(task);
                            break;
                        case LONG:
                            this.hosts.get(2).addTask(task);
                            break;
                        default:
                            System.out.println("NOT SITA");
                            break;
                    }
                }

            }

            // IMPLEMENTARE SQ
            if (this.algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {

                int index = 0;
                int size_min = Integer.MAX_VALUE;


                    for (int i = 0; i < hosts.size(); i++) {
                        Host host = hosts.get(i);
                        int size_host = host.getQueueSize();

                        if (size_host < size_min || (size_host == size_min && host.getId() < hosts.get(index).getId())) {
                            size_min = size_host;
                            index = i;
                        }
                    }


                this.hosts.get(index).addTask(task);
            }

            // IMPLEMENTARE LWL
            if (this.algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
            int index = 0;
            long work_left_min = Integer.MAX_VALUE;

                for (int i = 0; i < hosts.size(); i++) {
                    Host host = hosts.get(i);
                    long size_host = host.getWorkLeft();

                    if (size_host < work_left_min || (size_host == work_left_min && host.getId() < hosts.get(index).getId())) {
                        work_left_min = size_host;
                        index = i;
                    }
                }

            this.hosts.get(index).addTask(task);
            }
        }
        }
    }

