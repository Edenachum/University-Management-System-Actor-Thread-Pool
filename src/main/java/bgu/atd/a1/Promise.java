package bgu.atd.a1;
import javax.security.auth.callback.Callback;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
/**
 * this class represents a deferred result i.e., an object that eventually will
 * be resolved to hold a result of some operation, the class allows for getting
 * the result once it is available and registering a callback that will be
 * called once the result is available.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 * @param <T>
 *            the result type, <boolean> resolved - initialized ;
 */
public class Promise<T>{
	private boolean isResolved;
	private T result;
	private List<callback> subscribedCallbacks;


	public Promise(){
		isResolved = false;
		this.result = null;
		this.subscribedCallbacks = new ArrayList<>();
	}


	/**
	 *
	 * @return the resolved value if such exists (i.e., if this object has been
	 *         {@link #resolve(java.lang.Object)}ed 
	 * @throws IllegalStateException
	 *             in the case where this method is called and this object is
	 *             not yet resolved
	 */
	public synchronized T get() {
		if (isResolved)
			return result;
		throw new IllegalStateException("the promise has not been resolved");
	}

	/**
	 *
	 * @return true if this object has been resolved - i.e., if the method
	 *         {@link #resolve(java.lang.Object)} has been called on this object
	 *         before.
	 */
	public synchronized boolean isResolved() {
		return isResolved;
	}


	/**
	 * resolve this promise object - from now on, any call to the method
	 * {@link #get()} should return the given value
	 *
	 * Any callbacks that were registered to be notified when this object is
	 * resolved via the {@link #subscribe(callback)} method should
	 * be executed before this method returns
	 *
     * @throws IllegalStateException
     * 			in the case where this object is already resolved
	 * @param value
	 *            - the value to resolve this promise object with
	 */
	public synchronized void resolve(T value){
		if(isResolved)
			throw new IllegalStateException (" the promise is already resolved");
		isResolved = true;
		this.result = value;
		Iterator<callback> iterator = subscribedCallbacks.iterator();
		while (iterator.hasNext()){
			callback callback = iterator.next();
			callback.call();
		}
	}

	/**
	 * add a callback to be called when this object is resolved. If while
	 * calling this method the object is already resolved - the callback should
	 * be called immediately
	 *
	 * Note that in any case, the given callback should never get called more
	 * than once, in addition, in order to avoid memory leaks - once the
	 * callback got called, this object should not hold its reference any
	 * longer.
	 *
	 * @param callback
	 *            the callback to be called when the promise object is resolved
	 */
	// why synchronized? if one thread entered and isResolve at this moment is false and there is contact switch and another
	// thread resolve the promise, this thread will trigger all the callback that had subscribed but not this current callback.
	// when contact switch occurred again and the first thread will continue, he will push the callback into the queue but since result function
	// already called, this callback will never be triggered

	public synchronized void subscribe(callback callback) {
		if(isResolved){
			callback.call();
		}
		this.subscribedCallbacks.add(callback);
	}
}
