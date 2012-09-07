/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.mbm.phimp.me.gallery3d.media;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * An abstract class for the interface <code>Cancelable</code>. Subclass can
 * simply override the <code>execute()</code> function to provide an
 * implementation of <code>Cancelable</code>.
 */
public abstract class BaseCancelable<T> implements Cancelable<T> {

    /**
     * The state of the task, possible transitions are:
     * 
     * <pre>
     *     INITIAL -&gt; CANCELED
     *     EXECUTING -&gt; COMPLETE, CANCELING, ERROR, CANCELED
     *     CANCELING -&gt; CANCELED
     * </pre>
     * 
     * When the task stop, it must be end with one of the following states:
     * COMPLETE, CANCELED, or ERROR;
     */
    private static final int STATE_INITIAL = (1 << 0);
    private static final int STATE_EXECUTING = (1 << 1);
    private static final int STATE_CANCELING = (1 << 2);
    private static final int STATE_CANCELED = (1 << 3);
    private static final int STATE_ERROR = (1 << 4);
    private static final int STATE_COMPLETE = (1 << 5);

    private int mState = STATE_INITIAL;

    private Throwable mError;
    private T mResult;
    private Cancelable<?> mCurrentTask;

    protected abstract T execute() throws Exception;

    /**
     * Frees the result (which is not null) when the task has been canceled.
     */
    protected void freeCanceledResult(T result) {
        // Do nothing by default;
    }

    private boolean isInStates(int states) {
        return (states & mState) != 0;
    }

    private T handleTerminalStates() throws ExecutionException {
        if (mState == STATE_CANCELED) {
            throw new CancellationException();
        }
        if (mState == STATE_ERROR) {
            throw new ExecutionException(mError);
        }
        if (mState == STATE_COMPLETE)
            return mResult;
        throw new IllegalStateException();
    }

    public synchronized void await() throws InterruptedException {
        while (!isInStates(STATE_COMPLETE | STATE_CANCELED | STATE_ERROR)) {
            wait();
        }
    }

    public final T get() throws InterruptedException, ExecutionException {
        synchronized (this) {
            if (mState != STATE_INITIAL) {
                await();
                return handleTerminalStates();
            }
            mState = STATE_EXECUTING;
        }
        try {
            mResult = execute();
        } catch (CancellationException e) {
            mState = STATE_CANCELED;
        } catch (InterruptedException e) {
            mState = STATE_CANCELED;
        } catch (Throwable error) {
            synchronized (this) {
                if (mState != STATE_CANCELING) {
                    mError = error;
                    mState = STATE_ERROR;
                }
            }
        }
        synchronized (this) {
            if (mState == STATE_CANCELING)
                mState = STATE_CANCELED;
            if (mState == STATE_EXECUTING)
                mState = STATE_COMPLETE;
            notifyAll();
            if (mState == STATE_CANCELED && mResult != null) {
                freeCanceledResult(mResult);
            }
            return handleTerminalStates();
        }
    }

    /**
     * Requests the task to be canceled.
     * 
     * @return true if the task is running and has not been canceled; false
     *         otherwise
     */

    public synchronized boolean requestCancel() {
        if (mState == STATE_INITIAL) {
            mState = STATE_CANCELED;
            notifyAll();
            return false;
        }
        if (mState == STATE_EXECUTING) {
            if (mCurrentTask != null)
                mCurrentTask.requestCancel();
            mState = STATE_CANCELING;
            return true;
        }
        return false;
    }
}
