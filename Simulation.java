import java.util.PriorityQueue;
import java.util.Random;

class Process {
    double serviceTime;
    double arrivalTime;
    double startTime;
    double completionTime;
}

class Event {
    static final int ARR = 0;
    static final int DEP = 1;

    int type;
    double time;
    Process process;
}

class Simulation {
    static double randomFloat() {
        return new Random().nextFloat();
    }

    static double getInterarrivalTime(double lambda) {
        return -(1 / lambda) * Math.log(randomFloat());
    }

    static double getServiceTime(double averageServiceTime) {
        return -(1.0 / averageServiceTime) * Math.log(randomFloat());
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Need 2 parameters: <avgServiceTime> <avgArrivalRate(lambda)>");
            return;
        }

        double averageServiceTime = Double.parseDouble(args[0]);
        int lambda = Integer.parseInt(args[1]);

        PriorityQueue<Event> eventQueue = new PriorityQueue<>((e1, e2) -> Double.compare(e1.time, e2.time));

        int numberProcess = 0;
        int numberReadyQueue = 0;
        int completedProcess = 0;
        double totalTime = 0;
        //double totalBusyTime = 0;
        double totalCPU = 0;
        double totalTurnaround = 0;
        double lastEvent = 0;

        Random random = new Random();

        Event firstEvent = new Event();
        firstEvent.type = Event.ARR;
        firstEvent.time = totalTime + getInterarrivalTime(lambda);
        eventQueue.add(firstEvent);

        while (completedProcess < 10000) {
            Event nextEvent = eventQueue.poll();
            totalTime = nextEvent.time;

            totalCPU += (totalTime - lastEvent) * numberProcess;
            lastEvent = totalTime;

            if (nextEvent.type == Event.ARR) {
                Event nextArrival = new Event();
                nextArrival.type = Event.ARR;
                nextArrival.time = totalTime + getInterarrivalTime(lambda);
                eventQueue.add(nextArrival);

                double serviceTime = getServiceTime(averageServiceTime);

                Process thisProcess = new Process();
                thisProcess.arrivalTime = totalTime;
                thisProcess.serviceTime = serviceTime;

                numberProcess++;
                numberReadyQueue++;

                if (numberReadyQueue == 1) {
                    thisProcess.startTime = totalTime;

                    Event departure = new Event();
                    departure.type = Event.DEP;
                    departure.time = totalTime + serviceTime;
                    departure.process = thisProcess;
                    eventQueue.add(departure);

                    numberReadyQueue--;
                }
            } else if (nextEvent.type == Event.DEP) {
                Process thisProcess = nextEvent.process;

                thisProcess.startTime = nextEvent.time - thisProcess.serviceTime;
                thisProcess.completionTime = nextEvent.time;

                totalTurnaround += thisProcess.completionTime - thisProcess.arrivalTime;
                completedProcess++;
                numberProcess--;
                numberReadyQueue--;

                if (numberReadyQueue > 0) {
                    Process nextProcess = thisProcess;
                    nextProcess.startTime = totalTime;

                    Event departure = new Event();
                    departure.type = Event.DEP;
                    departure.time = totalTime + nextProcess.serviceTime;
                    eventQueue.add(departure);

                    numberReadyQueue--;
                } else {
                    numberReadyQueue = 0;
                }
            }
        }

        double averageTurnaround = totalTurnaround / 10000;
        double totalThroughput = 10000 / totalTime;
        double averageCPU = (((totalCPU / totalTime) / 1000) * 100);
        double averageReadyQueue = totalCPU / totalTime;

        System.out.println("\nAverage Turnaround Time: " + averageTurnaround + " second");
        System.out.println("Total Throughput: " + totalThroughput + " processes per second");
        System.out.println("Average CPU Utilization: " + averageCPU + " %");
        System.out.println("Average Number Processes in Ready Queue: " + averageReadyQueue + " processes\n");
    }
}
