package com.codecool.demo.repository;

import com.codecool.demo.model.LocalFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalFileRepository extends JpaRepository<LocalFile, Long> {}
