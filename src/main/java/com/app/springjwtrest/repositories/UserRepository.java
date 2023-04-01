package com.app.springjwtrest.repositories;

import com.app.springjwtrest.models.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {

 
    @Override
    public Optional<UserEntity> findById(String id);

 
    @Override
    public <S extends UserEntity> S save(S entity);

    
    @Override
    public List<UserEntity> findAll();

  
    @Override
    public void deleteById(String id);

}
