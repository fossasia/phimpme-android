/*
 * Copyright (c) 2008-2009, Motorola, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - Neither the name of the Motorola, Inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package it.kynetics.bluetooth.sendfile.opp;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Exposes constants used to interact with the Bluetooth Share manager's content
 * provider.
 * @hide
 */

public final class BluetoothShare implements BaseColumns {
    private BluetoothShare() {
    }

    /**
     * The permission to access the Bluetooth Share Manager
     */
    public static final String PERMISSION_ACCESS = "android.permission.ACCESS_BLUETOOTH_SHARE";

    /**
     * The content:// URI for the data table in the provider
     */
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.bluetooth.opp/btopp");

    /**
     * Broadcast Action: this is sent by the Bluetooth Share component to
     * transfer complete. The request detail could be retrieved by app * as _ID
     * is specified in the intent's data.
     */
    public static final String TRANSFER_COMPLETED_ACTION = "android.btopp.intent.action.TRANSFER_COMPLETE";

    /**
     * This is sent by the Bluetooth Share component to indicate there is an
     * incoming file need user to confirm.
     */
    public static final String INCOMING_FILE_CONFIRMATION_REQUEST_ACTION = "android.btopp.intent.action.INCOMING_FILE_NOTIFICATION";

    /**
     * This is sent by the Bluetooth Share component to indicate there is an
     * incoming file request timeout and need update UI.
     */
    public static final String USER_CONFIRMATION_TIMEOUT_ACTION = "android.btopp.intent.action.USER_CONFIRMATION_TIMEOUT";

    /**
     * The name of the column containing the URI of the file being
     * sent/received.
     * <P>
     * Type: TEXT
     * </P>
     * <P>
     * Owner can Init/Read
     * </P>
     */
    public static final String URI = "uri";

    /**
     * The name of the column containing the filename that the incoming file
     * request recommends. When possible, the Bluetooth Share manager will
     * attempt to use this filename, or a variation, as the actual name for the
     * file.
     * <P>
     * Type: TEXT
     * </P>
     * <P>
     * Owner can Init/Read
     * </P>
     */
    public static final String FILENAME_HINT = "hint";

    /**
     * The name of the column containing the filename where the shared file was
     * actually stored.
     * <P>
     * Type: TEXT
     * </P>
     * <P>
     * Owner can Read
     * </P>
     */
    public static final String _DATA = "_data";

    /**
     * The name of the column containing the MIME type of the shared file.
     * <P>
     * Type: TEXT
     * </P>
     * <P>
     * Owner can Init/Read
     * </P>
     */
    public static final String MIMETYPE = "mimetype";

    /**
     * The name of the column containing the direction (Inbound/Outbound) of the
     * transfer. See the DIRECTION_* constants for a list of legal values.
     * <P>
     * Type: INTEGER
     * </P>
     * <P>
     * Owner can Init/Read
     * </P>
     */
    public static final String DIRECTION = "direction";

    /**
     * The name of the column containing Bluetooth Device Address that the
     * transfer is associated with.
     * <P>
     * Type: TEXT
     * </P>
     * <P>
     * Owner can Init/Read
     * </P>
     */
    public static final String DESTINATION = "destination";

    /**
     * The name of the column containing the flags that controls whether the
     * transfer is displayed by the UI. See the VISIBILITY_* constants for a
     * list of legal values.
     * <P>
     * Type: INTEGER
     * </P>
     * <P>
     * Owner can Init/Read/Write
     * </P>
     */
    public static final String VISIBILITY = "visibility";

    /**
     * The name of the column containing the current user confirmation state of
     * the transfer. Applications can write to this to confirm the transfer. the
     * USER_CONFIRMATION_* constants for a list of legal values.
     * <P>
     * Type: INTEGER
     * </P>
     * <P>
     * Owner can Init/Read/Write
     * </P>
     */
    public static final String USER_CONFIRMATION = "confirm";

    /**
     * The name of the column containing the current status of the transfer.
     * Applications can read this to follow the progress of each download. See
     * the STATUS_* constants for a list of legal values.
     * <P>
     * Type: INTEGER
     * </P>
     * <P>
     * Owner can Read
     * </P>
     */
    public static final String STATUS = "status";

    /**
     * The name of the column containing the total size of the file being
     * transferred.
     * <P>
     * Type: INTEGER
     * </P>
     * <P>
     * Owner can Read
     * </P>
     */
    public static final String TOTAL_BYTES = "total_bytes";

    /**
     * The name of the column containing the size of the part of the file that
     * has been transferred so far.
     * <P>
     * Type: INTEGER
     * </P>
     * <P>
     * Owner can Read
     * </P>
     */
    public static final String CURRENT_BYTES = "current_bytes";

    /**
     * The name of the column containing the timestamp when the transfer is
     * initialized.
     * <P>
     * Type: INTEGER
     * </P>
     * <P>
     * Owner can Read
     * </P>
     */
    public static final String TIMESTAMP = "timestamp";

    /**
     * This transfer is outbound, e.g. share file to other device.
     */
    public static final int DIRECTION_OUTBOUND = 0;

    /**
     * This transfer is inbound, e.g. receive file from other device.
     */
    public static final int DIRECTION_INBOUND = 1;

    /**
     * This transfer is waiting for user confirmation.
     */
    public static final int USER_CONFIRMATION_PENDING = 0;

    /**
     * This transfer is confirmed by user.
     */
    public static final int USER_CONFIRMATION_CONFIRMED = 1;

    /**
     * This transfer is auto-confirmed per previous user confirmation.
     */
    public static final int USER_CONFIRMATION_AUTO_CONFIRMED = 2;

    /**
     * This transfer is denied by user.
     */
    public static final int USER_CONFIRMATION_DENIED = 3;

    /**
     * This transfer is timeout before user action.
     */
    public static final int USER_CONFIRMATION_TIMEOUT = 4;

    /**
     * This transfer is visible and shows in the notifications while in progress
     * and after completion.
     */
    public static final int VISIBILITY_VISIBLE = 0;

    /**
     * This transfer doesn't show in the notifications.
     */
    public static final int VISIBILITY_HIDDEN = 1;

    /**
     * Returns whether the status is informational (i.e. 1xx).
     */
    public static boolean isStatusInformational(int status) {
        return (status >= 100 && status < 200);
    }

    /**
     * Returns whether the transfer is suspended. (i.e. whether the transfer
     * won't complete without some action from outside the transfer manager).
     */
    public static boolean isStatusSuspended(int status) {
        return (status == STATUS_PENDING);
    }

    /**
     * Returns whether the status is a success (i.e. 2xx).
     */
    public static boolean isStatusSuccess(int status) {
        return (status >= 200 && status < 300);
    }

    /**
     * Returns whether the status is an error (i.e. 4xx or 5xx).
     */
    public static boolean isStatusError(int status) {
        return (status >= 400 && status < 600);
    }

    /**
     * Returns whether the status is a client error (i.e. 4xx).
     */
    public static boolean isStatusClientError(int status) {
        return (status >= 400 && status < 500);
    }

    /**
     * Returns whether the status is a server error (i.e. 5xx).
     */
    public static boolean isStatusServerError(int status) {
        return (status >= 500 && status < 600);
    }

    /**
     * Returns whether the transfer has completed (either with success or
     * error).
     */
    public static boolean isStatusCompleted(int status) {
        return (status >= 200 && status < 300) || (status >= 400 && status < 600);
    }

    /**
     * This transfer hasn't stated yet
     */
    public static final int STATUS_PENDING = 190;

    /**
     * This transfer has started
     */
    public static final int STATUS_RUNNING = 192;

    /**
     * This transfer has successfully completed. Warning: there might be other
     * status values that indicate success in the future. Use isSucccess() to
     * capture the entire category.
     */
    public static final int STATUS_SUCCESS = 200;

    /**
     * This request couldn't be parsed. This is also used when processing
     * requests with unknown/unsupported URI schemes.
     */
    public static final int STATUS_BAD_REQUEST = 400;

    /**
     * This transfer is forbidden by target device.
     */
    public static final int STATUS_FORBIDDEN = 403;

    /**
     * This transfer can't be performed because the content cannot be handled.
     */
    public static final int STATUS_NOT_ACCEPTABLE = 406;

    /**
     * This transfer cannot be performed because the length cannot be determined
     * accurately. This is the code for the HTTP error "Length Required", which
     * is typically used when making requests that require a content length but
     * don't have one, and it is also used in the client when a response is
     * received whose length cannot be determined accurately (therefore making
     * it impossible to know when a transfer completes).
     */
    public static final int STATUS_LENGTH_REQUIRED = 411;

    /**
     * This transfer was interrupted and cannot be resumed. This is the code for
     * the OBEX error "Precondition Failed", and it is also used in situations
     * where the client doesn't have an ETag at all.
     */
    public static final int STATUS_PRECONDITION_FAILED = 412;

    /**
     * This transfer was canceled
     */
    public static final int STATUS_CANCELED = 490;

    /**
     * This transfer has completed with an error. Warning: there will be other
     * status values that indicate errors in the future. Use isStatusError() to
     * capture the entire category.
     */
    public static final int STATUS_UNKNOWN_ERROR = 491;

    /**
     * This transfer couldn't be completed because of a storage issue.
     * Typically, that's because the file system is missing or full.
     */
    public static final int STATUS_FILE_ERROR = 492;

    /**
     * This transfer couldn't be completed because of no sdcard.
     */
    public static final int STATUS_ERROR_NO_SDCARD = 493;

    /**
     * This transfer couldn't be completed because of sdcard full.
     */
    public static final int STATUS_ERROR_SDCARD_FULL = 494;

    /**
     * This transfer couldn't be completed because of an unspecified un-handled
     * OBEX code.
     */
    public static final int STATUS_UNHANDLED_OBEX_CODE = 495;

    /**
     * This transfer couldn't be completed because of an error receiving or
     * processing data at the OBEX level.
     */
    public static final int STATUS_OBEX_DATA_ERROR = 496;

    /**
     * This transfer couldn't be completed because of an error when establishing
     * connection.
     */
    public static final int STATUS_CONNECTION_ERROR = 497;

}
