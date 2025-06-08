package com.codecool.demo.repository;

import com.codecool.demo.model.LocalFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for {@link LocalFile} entities representing both files and directories.
 *
 * <p>Provides CRUD operations for:
 *
 * <ul>
 *   <li>Individual file entries
 *   <li>Directory entries (through inheritance)
 * </ul>
 *
 * <p>Key relationships:
 *
 * <ul>
 *   <li>Directories contain collections of LocalFiles (parent-child hierarchy)
 *   <li>Files maintain reference to parent Directory
 * </ul>
 */
@Repository
public interface LocalFileRepository extends JpaRepository<LocalFile, Long> {}
