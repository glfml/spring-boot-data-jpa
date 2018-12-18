package com.meli.datajpa.app.models.dao;

import com.meli.datajpa.app.models.entity.Cliente;
import org.springframework.data.repository.CrudRepository;
//JpaRepository es un poco mejor que Crud, implementa paginado, etc.
public interface IClienteDao extends CrudRepository<Cliente, Long> {

}
