package com.dryver.Utility;

/**
 * Boolean Callback interface. That is it has a success or fail state.
 */

public interface IBooleanCallBack {
    /**
     * Should be implemented as success callback method
     */
    void success();

    /**
     * Should be implemented as failure callback method
     */
    void failure();
}
