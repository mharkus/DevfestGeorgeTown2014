package com.marctan.gdgpenangwear.wolfram;

public interface ExecuteQueryResponseCallback {
    public void onSuccess(Pod pod);
    public void onFailure(String errorMessage);
}
