package com.rstejskalprojects.reservationsystem.util;

public interface DtoMapper<D, T> {

    T map(D dto);
}
