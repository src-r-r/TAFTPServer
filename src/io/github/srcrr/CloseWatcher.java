package io.github.srcrr;

import java.util.ArrayList;

/**
 * Watches for any dead threads (i.e. any clients that are closed) and removes
 * them from the threads arraylist.
 * @author jordan
 *
 */
class CloseWatcher {
	
	private ArrayList<TAThread> mThreads = null;

	public CloseWatcher(ArrayList<TAThread> threads) {
		mThreads  = threads;
	}
	
	public void onClose(TAThread thread) {
		int i = mThreads.indexOf(thread);
		mThreads.remove(thread);
		System.err.println("Closed thread " + i);
		System.err.println("Thread Arraylist contains " + mThreads.size() + " threads");
	}
}