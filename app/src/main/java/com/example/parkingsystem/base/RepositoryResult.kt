package com.example.parkingsystem.base

interface RepositoryResult<T> {
    fun result(result: Result<T>)
}