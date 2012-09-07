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

import java.util.concurrent.ExecutionException;

/**
 * The interface for all the tasks that could be canceled.
 */
public interface Cancelable<T> {
    /*
     * Requests this <code>Cancelable</code> to be canceled. This function will
     * return <code>true</code> if and only if the task is originally running
     * and now begin requested for cancel.
     * 
     * If subclass need to do more things to cancel the task. It can override
     * the code like this: <pre>
     * 
     * @Override public boolean requestCancel() { if (super.requestCancel()) {
     * // do necessary work to cancel the task return true; } return false; }
     * </pre>
     */
    public boolean requestCancel();

    public void await() throws InterruptedException;

    /**
     * Gets the results of this <code>Cancelable</code> task.
     * 
     * @throws ExecutionException
     *             if exception is thrown during the execution of the task
     */
    public T get() throws InterruptedException, ExecutionException;
}