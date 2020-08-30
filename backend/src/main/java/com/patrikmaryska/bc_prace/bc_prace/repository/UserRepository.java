package com.patrikmaryska.bc_prace.bc_prace.repository;

import com.patrikmaryska.bc_prace.bc_prace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository{
}
