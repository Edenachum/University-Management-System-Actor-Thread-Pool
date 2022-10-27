package bgu.atd.a1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {

    private ExecutorService threadsPool;
    private HashMap<String, PrivateState> actorsPrivateStates;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Action>> actorsQueue;
    private ConcurrentHashMap<String, Boolean> isActorTreatedQueue;
    private int numOfThreads;
    private int numOfActions;
    private boolean shouldTerminate;
    private Object shouldTerminateOb;


    /**
     * creates a {@link ActorThreadPool} which has nthreads. Note, threads
     * should not get started until calling to the {@link #start()} method.
     * <p>
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this thread
     *                 pool
     */
    public ActorThreadPool(int nthreads) {
        this.threadsPool = Executors.newFixedThreadPool(nthreads);
        this.actorsPrivateStates = new HashMap<String, PrivateState>();
        this.actorsQueue = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Action>>();
        this.isActorTreatedQueue = new ConcurrentHashMap<String, Boolean>();
        this.numOfThreads = nthreads;
        this.numOfActions = 0;
        shouldTerminate = false;
        this.shouldTerminateOb = new Object();


    }

    /**
     * getter for actors
     *
     * @return actors
     */
    public synchronized Map<String, PrivateState> getActors() {
        return actorsPrivateStates;
    }

    /**
     * getter for actor's private state
     *
     * @param actorId actor's id
     * @return actor's private state
     */
    public synchronized PrivateState getPrivateState(String actorId) throws Exception  {
        PrivateState actorPrivateState = actorsPrivateStates.get(actorId);
        if (actorPrivateState == null) {
            throw new Exception("there is no private state for this actor");
        }
        return actorPrivateState;
    }


    /**
     * submits an action into an actor to be executed by a thread belongs to
     * this thread pool
     *
     * @param action     the action to execute
     * @param actorId    corresponding actor's id
     * @param actorState actor's private state (actor's information)
     */
    public synchronized void submit(Action<?> action, String actorId, PrivateState actorState) {
        ConcurrentLinkedQueue actorQueue = actorsQueue.get(actorId);
        if (actorQueue == null) {
            actorQueue = new ConcurrentLinkedQueue();
            actorsQueue.put(actorId, actorQueue);
            actorsPrivateStates.put(actorId, actorState);
            isActorTreatedQueue.put(actorId, false);
        }
        actorQueue.add(action);
        this.updateNumOfActions(1);
        this.notify();

    }

    /**
     * closes the thread pool - this method interrupts all the threads and waits
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     * <p>
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is interrupted
     */

    // need to see how to implement this function!!! we need to do shut down while numOfActions == 0 -> how we do that??
    public void shutdown() throws InterruptedException {

        shouldTerminate = true;
        synchronized (this) {
            this.notify();
        }
        synchronized (shouldTerminateOb) {
            if (numOfActions > 0)
                shouldTerminateOb.wait();
        }
        threadsPool.shutdown();
        while (!threadsPool.isTerminated()) {
        }
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
        Thread thread = new Thread(() -> {
            while (!shouldTerminate) {
                synchronized (this) {
                    while (numOfActions == 0 && !shouldTerminate) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
                for (Map.Entry<String, Boolean> entry : isActorTreatedQueue.entrySet()) {
                    String actorID = entry.getKey();
                    if (!entry.getValue()) {
                        if (!actorsQueue.get(actorID).isEmpty()) {
                            isActorTreatedQueue.put(actorID, true);
                            this.updateNumOfActions(-1);
                            threadsPool.execute(() ->
                            {
                                try {
                                    actorsQueue.get(actorID).poll().handle(this, actorID, getPrivateState(actorID));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            if (numOfActions == 0)
                                break;
                        }
                    }
                }
            }
            while (numOfActions > 0) {
                for (Map.Entry<String, Boolean> entry : isActorTreatedQueue.entrySet()) {
                    String actorID = entry.getKey();
                    if (!entry.getValue()) {
                        if (!actorsQueue.get(actorID).isEmpty()) {
                            isActorTreatedQueue.put(actorID, true);
                            this.updateNumOfActions(-1);
                            threadsPool.execute(() ->
                            {
                                try {
                                    actorsQueue.get(actorID).poll().handle(this, actorID, getPrivateState(actorID));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
            }
            synchronized ((shouldTerminateOb)) {
                shouldTerminateOb.notify();
            }
        });
        thread.start();


    }

    public synchronized void updateNumOfActions(int addition) {
        this.numOfActions += addition;
    }

    public void makeActorAvailable(String actorID) {
        isActorTreatedQueue.put(actorID, false);
    }

    public Boolean getShouldTerminate() {
        return shouldTerminate;
    }
}
