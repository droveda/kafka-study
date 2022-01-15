package com.droveda.consumer;

public interface ServiceFactory<T> {

    ConsumerService<T> create() throws Exception;

}
