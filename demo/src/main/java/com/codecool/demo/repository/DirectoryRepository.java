package com.codecool.demo.repository;

import com.codecool.demo.model.Directory;
import com.codecool.demo.model.LocalFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Specialized repository for {@link Directory} entities.
 *
 * <p>Extends {@link LocalFileRepository} capabilities with directory-specific operations. Handles
 * persistence of directory structures including:
 *
 * <ul>
 *   <li>Directory metadata (name, path)
 *   <li>Aggregated size calculations
 *   <li>Parent-child relationships with nested files/directories
 * </ul>
 *
 * <p>Note: Directories are subtype of {@link LocalFile} with additional containment relationships.
 */
@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {}
