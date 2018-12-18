package com.meli.datajpa.app.models.dao;

import com.meli.datajpa.app.models.entity.Cliente;
import java.util.List;

public interface IClienteDao {

    public List<Cliente> findAll();

    public void save(Cliente cliente);
}
