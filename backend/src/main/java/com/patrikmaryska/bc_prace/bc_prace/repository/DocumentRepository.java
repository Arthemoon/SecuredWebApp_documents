package com.patrikmaryska.bc_prace.bc_prace.repository;

import com.patrikmaryska.bc_prace.bc_prace.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Jpa21Utils;

public interface DocumentRepository extends JpaRepository<Document, Long>, DocumentRepositoryCustom {
}
