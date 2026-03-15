package com.pdks.mobile.api;

import java.io.IOException;

/**
 * İnternet bağlantısı olmadığında fırlatılan özel exception.
 * OkHttp interceptor tarafından fırlatılır, BaseApiCallback tarafından yakalanır.
 */
public class NoConnectivityException extends IOException {

    public NoConnectivityException() {
        super("İnternet bağlantısı yok");
    }
}
