package com.rstejskalprojects.reservationsystem.service;


public interface TokenService<T> {
    void saveToken(T token);

    T getToken(String token);

    void setConfirmedAt(T token);
}
