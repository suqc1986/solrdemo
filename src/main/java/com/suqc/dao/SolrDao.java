package com.suqc.dao;

public interface SolrDao<T> {
    public T getById(Long id) throws Exception;
    public void update(T t) throws Exception;
    public void deleteById(Long id) throws Exception;
    public void save(T t) throws Exception;
}
