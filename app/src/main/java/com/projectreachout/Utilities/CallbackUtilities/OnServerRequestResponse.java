package com.projectreachout.Utilities.CallbackUtilities;

import java.util.List;

public interface OnServerRequestResponse {
    void onSuccess(int responseCode, List<?> response, String msg);
}
