package com.codecool.demo.repository;

import com.codecool.demo.model.DiffRequest;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for persisting and retrieving file comparison requests.
 *
 * <p>Manages {@link DiffRequest} entities which represent:
 *
 * <ul>
 *   <li>User-initiated comparison operations
 *   <li>Timestamps for historical tracking
 *   <li>Associations with compared file/directory pairs
 * </ul>
 *
 * <p>Persisted data includes:
 *
 * <ul>
 *   <li>Username of requester
 *   <li>References to compared file/directory entities
 *   <li>Automatic timestamping of request creation
 * </ul>
 */
public interface DiffRequestRepository extends JpaRepository<DiffRequest, Long> {}
